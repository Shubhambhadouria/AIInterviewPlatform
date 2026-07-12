package com.aiinterviewcoach.modules.questionbank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedPreparationAnswer(String shortAnswer, String detailedAnswer, String internalWorking,
		String codeExample, String projectUsage, String starAnswer, String businessImpact, String followUpQuestions,
		String commonMistakes, String whatNotToSay, String productCompanyExpectation,
		String serviceCompanyExpectation) {
}
