package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeParseResponse;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;
import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;
import com.aiinterviewcoach.modules.questionbank.repository.CandidateProfileRepository;
import com.aiinterviewcoach.modules.questionbank.repository.ResumeRepository;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;
import com.aiinterviewcoach.modules.questionbank.service.ResumeAiParser;
import com.aiinterviewcoach.modules.questionbank.service.ResumeParsingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeParsingServiceImpl implements ResumeParsingService {

	private final ResumeRepository resumeRepository;
	private final FileStorageService fileStorageService;
	private final ResumeAiParser resumeAiParser;
	private final CandidateProfileRepository candidateProfileRepository;

	@Override
	@Transactional
	public ResumeParseResponse parse(UUID resumeId, String userEmail) {

		Resume resume = resumeRepository.findByIdAndUserEmail(resumeId, userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));

		if (resume.getStatus() != ResumeStatus.UPLOADED && resume.getStatus() != ResumeStatus.PARSING_FAILED) {

			throw new BadRequestException("Resume cannot be parsed when status is: " + resume.getStatus());
		}

		resume.setStatus(ResumeStatus.PARSING);
		resumeRepository.save(resume);

		try {
			CandidateProfile profile = parse(resume);

			resume.setStatus(ResumeStatus.PARSED);
			resume.setParsedAt(LocalDateTime.now());

			resumeRepository.save(resume);

			return new ResumeParseResponse(resume.getId(), profile.getId(), resume.getStatus(), resume.getParsedAt(),
					"Resume parsed successfully.");

		} catch (RuntimeException exception) {
			resume.setStatus(ResumeStatus.PARSING_FAILED);
			resumeRepository.save(resume);

			throw exception;
		}
	}

	@Override
	@Transactional
	public CandidateProfile parse(Resume resume) {

		byte[] fileContent = fileStorageService.read(resume.getStorageKey());

		CandidateProfile parsedProfile = resumeAiParser.parse(fileContent, resume.getContentType());

		CandidateProfile candidateProfile = candidateProfileRepository.findByResumeId(resume.getId())
				.orElseGet(CandidateProfile::new);

		// Update simple fields explicitly
		candidateProfile.setResume(resume);
		candidateProfile.setFullName(resume.getUser().getFullName());
		candidateProfile.setEmail(parsedProfile.getEmail());
		candidateProfile.setProfessionalTitle(parsedProfile.getProfessionalTitle());
		candidateProfile.setProfessionalSummary(parsedProfile.getProfessionalSummary());
		candidateProfile.setTotalExperienceMonths(parsedProfile.getTotalExperienceMonths());
		candidateProfile.setStatus(CandidateProfileStatus.REVIEW_REQUIRED);

		// Preserve the existing Hibernate-managed collection instance
		candidateProfile.getProjects().clear();

		if (parsedProfile.getProjects() != null) {
			parsedProfile.getProjects().forEach(project -> {
				project.setCandidateProfile(candidateProfile);
				candidateProfile.getProjects().add(project);
			});
		}

		// Do the same for skills if orphanRemoval is enabled
		candidateProfile.getSkills().clear();

		if (parsedProfile.getSkills() != null) {
			parsedProfile.getSkills().forEach(skill -> {
				skill.setCandidateProfile(candidateProfile);
				candidateProfile.getSkills().add(skill);
			});
		}

		return candidateProfileRepository.save(candidateProfile);
	}

	@Override
	public ResumeParseResponse parseResume(UUID resumeId, String userEmail) {
		return parse(resumeId, userEmail);
	}
}