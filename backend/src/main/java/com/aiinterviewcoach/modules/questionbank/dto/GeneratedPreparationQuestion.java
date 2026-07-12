package com.aiinterviewcoach.modules.questionbank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedPreparationQuestion(String questionText, String difficulty, String questionType,
		String sourceSkill, String sourceProject, String resumeRelevance, GeneratedPreparationAnswer answer) {
}
