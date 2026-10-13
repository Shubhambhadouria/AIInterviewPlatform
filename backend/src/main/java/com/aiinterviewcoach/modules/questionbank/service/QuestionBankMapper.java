package com.aiinterviewcoach.modules.questionbank.service;

import org.springframework.stereotype.Component;

import com.aiinterviewcoach.modules.questionbank.dto.GeneratedQuestionBank;
import com.aiinterviewcoach.modules.questionbank.dto.QuestionAnswerResponse;
import com.aiinterviewcoach.modules.questionbank.dto.QuestionBankSummaryResponse;
import com.aiinterviewcoach.modules.questionbank.entity.QuestionBank;
import com.aiinterviewcoach.modules.questionbank.entity.QuestionBankQuestion;

@Component
public class QuestionBankMapper {

	public QuestionBankSummaryResponse toSummaryResponse(QuestionBank questionBank) {

		int totalSections = questionBank.getSections() != null ? questionBank.getSections().size() : 0;

		int totalQuestions = questionBank.getSections() == null ? 0
				: questionBank.getSections().stream().filter(section -> section.getQuestions() != null)
						.mapToInt(section -> section.getQuestions().size()).sum();

		return new QuestionBankSummaryResponse(questionBank.getId(), questionBank.getCandidateProfile().getId(),
				questionBank.getTitle(), questionBank.getStatus(), totalSections, totalQuestions,
				buildMessage(questionBank));
	}

	public QuestionAnswerResponse toQuestionResponse(QuestionBankQuestion question) {

		return new QuestionAnswerResponse(question.getId(), question.getCategory(), question.getQuestionText(),
				question.getShortAnswer(), question.getDetailedAnswer());
	}

	private String buildMessage(QuestionBank questionBank) {

		return switch (questionBank.getStatus()) {
		case GENERATING -> "Question bank generation is in progress.";
		case READY -> "Question bank generated successfully.";
		case GENERATION_FAILED -> questionBank.getGenerationError() != null ? questionBank.getGenerationError()
				: "Question bank generation failed.";
		default -> throw new IllegalArgumentException("Unexpected value: " + questionBank.getStatus());
		};
	}

	public void mapGeneratedContent(QuestionBank savedQuestionBank, GeneratedQuestionBank generatedQuestionBank) {
		savedQuestionBank.getSections().clear();
		int order = 0;
		for (var generatedSection : generatedQuestionBank.sections()) {
			if (generatedSection == null || generatedSection.name() == null || generatedSection.name().isBlank())
				throw new com.aiinterviewcoach.common.exception.AiProcessingException("AI returned an invalid section");
			var section = com.aiinterviewcoach.modules.questionbank.entity.QuestionSection.builder()
				.name(generatedSection.name()).category(generatedSection.category()).displayOrder(++order).build();
			for (var generated : generatedSection.questions()) {
				if (generated == null || generated.questionText() == null || generated.questionText().isBlank() ||
					generated.answer() == null || generated.answer().shortAnswer() == null || generated.answer().detailedAnswer() == null)
					throw new com.aiinterviewcoach.common.exception.AiProcessingException("AI returned an incomplete question or answer");
				com.aiinterviewcoach.modules.questionbank.enums.QuestionDifficulty difficulty;
				try { difficulty = com.aiinterviewcoach.modules.questionbank.enums.QuestionDifficulty.valueOf(generated.difficulty()); }
				catch (Exception ex) { throw new com.aiinterviewcoach.common.exception.AiProcessingException("AI returned invalid difficulty"); }
				var question = com.aiinterviewcoach.modules.questionbank.entity.PreparationQuestion.builder()
					.questionText(generated.questionText()).difficulty(difficulty).questionType(generated.questionType())
					.sourceSkill(generated.sourceSkill()).sourceProject(generated.sourceProject()).resumeRelevance(generated.resumeRelevance()).build();
				var answer = new com.aiinterviewcoach.modules.questionbank.entity.PreparationAnswer();
				answer.setShortAnswer(generated.answer().shortAnswer());
				answer.setDetailedAnswer(generated.answer().detailedAnswer());
				answer.setInternalWorking(generated.answer().internalWorking());
				answer.setCodeExample(generated.answer().codeExample());
				answer.setProjectUsage(generated.answer().projectUsage());
				answer.setStarAnswer(generated.answer().starAnswer());
				answer.setBusinessImpact(generated.answer().businessImpact());
				answer.setFollowUpQuestions(generated.answer().followUpQuestions());
				answer.setCommonMistakes(generated.answer().commonMistakes());
				answer.setWhatNotToSay(generated.answer().whatNotToSay());
				answer.setProductCompanyExpectation(generated.answer().productCompanyExpectation());
				answer.setServiceCompanyExpectation(generated.answer().serviceCompanyExpectation());
				question.setAnswer(answer);
				section.addQuestion(question);
			}
			savedQuestionBank.addSection(section);
		}
	}
}
