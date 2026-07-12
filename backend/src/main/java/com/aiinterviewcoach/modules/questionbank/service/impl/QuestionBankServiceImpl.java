package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinterviewcoach.common.exception.AiProcessingException;
import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.GenerateQuestionBankRequest;
import com.aiinterviewcoach.modules.questionbank.dto.GeneratedQuestionBank;
import com.aiinterviewcoach.modules.questionbank.dto.QuestionBankSummaryResponse;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.QuestionBank;
import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;
import com.aiinterviewcoach.modules.questionbank.enums.QuestionBankStatus;
import com.aiinterviewcoach.modules.questionbank.repository.CandidateProfileRepository;
import com.aiinterviewcoach.modules.questionbank.repository.QuestionBankRepository;
import com.aiinterviewcoach.modules.questionbank.service.QuestionBankAiGenerator;
import com.aiinterviewcoach.modules.questionbank.service.QuestionBankMapper;
import com.aiinterviewcoach.modules.questionbank.service.QuestionBankService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl implements QuestionBankService {

	private final CandidateProfileRepository candidateProfileRepository;

	private final QuestionBankRepository questionBankRepository;

	private final QuestionBankAiGenerator questionBankAiGenerator;

	private final QuestionBankMapper questionBankMapper;

	@Override
	@Transactional
	public QuestionBankSummaryResponse generate(GenerateQuestionBankRequest request, String userEmail) {

		CandidateProfile profile = candidateProfileRepository
				.findByIdAndResumeUserEmail(request.candidateProfileId(), userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

		validateCandidateProfile(profile);

		QuestionBank questionBank = QuestionBank.builder().user(profile.getResume().getUser()).candidateProfile(profile)
				.title(buildQuestionBankTitle(profile)).status(QuestionBankStatus.GENERATING).build();

		QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);

		try {

			GeneratedQuestionBank generatedQuestionBank = questionBankAiGenerator.generate(profile, request);

			validateGeneratedQuestionBank(generatedQuestionBank);

			questionBankMapper.mapGeneratedContent(savedQuestionBank, generatedQuestionBank);

			savedQuestionBank.setStatus(QuestionBankStatus.REVIEW_REQUIRED);
			savedQuestionBank.setGeneratedAt(LocalDateTime.now());
			savedQuestionBank.setGenerationError(null);

			QuestionBank completedQuestionBank = questionBankRepository.save(savedQuestionBank);

			int totalSections = completedQuestionBank.getSections().size();

			int totalQuestions = completedQuestionBank.getSections().stream()
					.mapToInt(section -> section.getQuestions().size()).sum();

			return new QuestionBankSummaryResponse(completedQuestionBank.getId(), profile.getId(),
					completedQuestionBank.getTitle(), completedQuestionBank.getStatus(), totalSections, totalQuestions,
					"Question bank generated successfully");

		} catch (RuntimeException exception) {

			savedQuestionBank.setStatus(QuestionBankStatus.GENERATION_FAILED);
			savedQuestionBank.setGenerationError(limitErrorMessage(exception.getMessage()));

			questionBankRepository.save(savedQuestionBank);

			throw exception;
		}
	}

	private void validateCandidateProfile(CandidateProfile profile) {

		if (profile.getStatus() != CandidateProfileStatus.CONFIRMED) {

			throw new BadRequestException("Candidate profile must be confirmed before generating a question bank");
		}

		boolean hasNoSkills = profile.getSkills() == null || profile.getSkills().isEmpty();

		boolean hasNoProjects = profile.getProjects() == null || profile.getProjects().isEmpty();

		if (hasNoSkills && hasNoProjects) {

			throw new BadRequestException("Candidate profile must contain at least one skill or project");
		}

		if (profile.getTargetRole() == null || profile.getTargetRole().isBlank()) {

			throw new BadRequestException("Target role is required before generating a question bank");
		}
	}

	private void validateGeneratedQuestionBank(GeneratedQuestionBank generatedQuestionBank) {

		if (generatedQuestionBank == null) {

			throw new AiProcessingException("AI returned an empty question bank response");
		}

		if (generatedQuestionBank.sections() == null || generatedQuestionBank.sections().isEmpty()) {

			throw new AiProcessingException("AI did not generate any question bank sections");
		}

		int totalQuestions = generatedQuestionBank.sections().stream()
				.filter(section -> section != null && section.questions() != null)
				.mapToInt(section -> section.questions().size()).sum();

		if (totalQuestions == 0) {

			throw new AiProcessingException("AI did not generate any preparation questions");
		}
	}

	private String buildQuestionBankTitle(CandidateProfile profile) {

		String targetRole = profile.getTargetRole();

		if (targetRole != null && !targetRole.isBlank()) {

			return targetRole.trim() + " Preparation Question Bank";
		}

		String professionalTitle = profile.getProfessionalTitle();

		if (professionalTitle != null && !professionalTitle.isBlank()) {

			return professionalTitle.trim() + " Preparation Question Bank";
		}

		return "Resume-Based Preparation Question Bank";
	}

	private String limitErrorMessage(String message) {

		if (message == null || message.isBlank()) {

			return "Unknown question bank generation error";
		}

		return message.length() <= 1000 ? message : message.substring(0, 1000);
	}
}
