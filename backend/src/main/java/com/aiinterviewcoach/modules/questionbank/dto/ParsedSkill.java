package com.aiinterviewcoach.modules.questionbank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParsedSkill(

		String name,

		String category,

		String proficiency,

		Integer yearsOfExperience,

		Boolean explicitlyMentioned) {
}
