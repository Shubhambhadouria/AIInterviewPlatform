package com.aiinterviewcoach.modules.interview.dto;

public record StartInterviewRequest(
		@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=200) String topic,
		@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=200) String targetRole,
		@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=50) String experienceLevel,
		@jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(20) Integer totalQuestions) {
}
