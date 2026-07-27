package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateQuestionBankRequest(@NotNull UUID candidateProfileId,
		@Min(1) @Max(20) Integer questionsPerSection, Boolean includeProjectQuestions,
		Boolean includeBehaviouralQuestions) {

	public GenerateQuestionBankRequest {
		questionsPerSection = questionsPerSection == null ? 5 : questionsPerSection;
		includeProjectQuestions = includeProjectQuestions == null || includeProjectQuestions;
		includeBehaviouralQuestions = includeBehaviouralQuestions == null || includeBehaviouralQuestions;
	}
}
