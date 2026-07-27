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
		// TODO Auto-generated method stub
		
	}
}