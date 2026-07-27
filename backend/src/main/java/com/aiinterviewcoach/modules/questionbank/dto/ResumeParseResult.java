package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeParseResult(

		String fullName,

		String email,

		String phone,

		String currentRole,

		Integer totalExperienceMonths,

		String professionalSummary,

		List<ParsedSkill> skills,

		List<ParsedProject> projects) {

	public ResumeParseResult {
		skills = skills == null ? new ArrayList<>() : skills;
		projects = projects == null ? new ArrayList<>() : projects;
	}
}
