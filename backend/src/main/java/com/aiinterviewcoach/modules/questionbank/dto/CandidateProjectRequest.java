package com.aiinterviewcoach.modules.questionbank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record CandidateProjectRequest(

		@NotBlank(message = "Project name is required") String projectName,
		String domain,

		String projectDescription,

		String candidateRole,

		String responsibilities,

		String businessImpact,

		LocalDate startDate,

		LocalDate endDate,

		boolean currentProject,

		List<@Valid ProjectTechnologyRequest> technologies

) {
}
