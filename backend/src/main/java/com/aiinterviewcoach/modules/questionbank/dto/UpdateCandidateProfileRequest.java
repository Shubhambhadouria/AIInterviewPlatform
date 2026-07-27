package com.aiinterviewcoach.modules.questionbank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateCandidateProfileRequest(

		String fullName,

		String professionalTitle,

		@Min(value = 0, message = "Experience cannot be negative") Integer totalExperienceMonths,
		String professionalSummary,

		String currentCompany,

		String currentRole,

		String targetRole,

		List<@Valid CandidateSkillRequest> skills,

		List<@Valid CandidateProjectRequest> projects

) {
}
