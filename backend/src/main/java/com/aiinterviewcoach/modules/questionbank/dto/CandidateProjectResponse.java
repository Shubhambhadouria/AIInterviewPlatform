package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CandidateProjectResponse(

		UUID id,

		String projectName,

		String domain,

		String projectDescription,

		String candidateRole,

		String responsibilities,

		String businessImpact,

		LocalDate startDate,

		LocalDate endDate,

		boolean currentProject,

		List<ProjectTechnologyResponse> technologies

) {
}
