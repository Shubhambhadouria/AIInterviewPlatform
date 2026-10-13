package com.aiinterviewcoach.modules.questionbank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateCandidateProfileRequest(

		@jakarta.validation.constraints.Size(max=200) String fullName,

		@jakarta.validation.constraints.Size(max=200) String professionalTitle,

		@Min(value = 0, message = "Experience cannot be negative") Integer totalExperienceMonths,
		@jakarta.validation.constraints.Size(max=10000) String professionalSummary,

		@jakarta.validation.constraints.Size(max=200) String currentCompany,

		@jakarta.validation.constraints.Size(max=200) String currentRole,

		@jakarta.validation.constraints.Size(max=200) String targetRole,

		@jakarta.validation.constraints.Size(max=100) List<@Valid CandidateSkillRequest> skills,

		@jakarta.validation.constraints.Size(max=30) List<@Valid CandidateProjectRequest> projects

) {
}
