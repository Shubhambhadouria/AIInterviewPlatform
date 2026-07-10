package com.aiinterviewcoach.modules.interview.dto;

import java.util.UUID;

public record InterviewQuestionResponse(
		UUID questionId,
		String questionText,
		String questionType,
		String difficulty) {
}
