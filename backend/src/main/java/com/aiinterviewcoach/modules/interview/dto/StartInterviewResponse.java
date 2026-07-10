package com.aiinterviewcoach.modules.interview.dto;

import java.util.List;
import java.util.UUID;

public record StartInterviewResponse(
		UUID sessionId,
		String title,
		String topic,
		String targetRole,
		String status,
		List<InterviewQuestionResponse> questions) {
}
