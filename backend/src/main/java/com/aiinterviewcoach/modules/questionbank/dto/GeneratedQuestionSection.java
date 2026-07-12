package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedQuestionSection(String name, String category, Integer displayOrder,
		List<GeneratedPreparationQuestion> questions) {
	public GeneratedQuestionSection {
		questions = questions == null ? List.of() : questions;
	}
}
