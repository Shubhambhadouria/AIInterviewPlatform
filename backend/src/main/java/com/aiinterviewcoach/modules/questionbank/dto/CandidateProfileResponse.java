package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;

public record CandidateProfileResponse(

		UUID id,

		UUID resumeId,

		String fullName,

		String professionalTitle,

		Integer totalExperienceMonths,

		String professionalSummary,

		String currentCompany,

		String currentRole,

		String targetRole,

		CandidateProfileStatus status,

		LocalDateTime confirmedAt,

		LocalDateTime createdAt,

		LocalDateTime updatedAt,

		List<CandidateSkillResponse> skills,

		List<CandidateProjectResponse> projects

) {
}
