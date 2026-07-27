package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedQuestionBank(String title, List<GeneratedQuestionSection> sections) {
	public GeneratedQuestionBank {
		sections = sections == null ? List.of() : sections;
	}
}
