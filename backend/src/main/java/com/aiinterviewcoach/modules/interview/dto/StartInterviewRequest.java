package com.aiinterviewcoach.modules.interview.dto;

public record StartInterviewRequest(
		String topic,
		String targetRole,
		String experienceLevel,
		Integer totalQuestions) {
}
