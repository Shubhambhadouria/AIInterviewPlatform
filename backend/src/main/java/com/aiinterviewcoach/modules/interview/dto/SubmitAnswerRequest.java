package com.aiinterviewcoach.modules.interview.dto;

import java.util.UUID;

public record SubmitAnswerRequest(
		UUID questionId,
		String answer) {
}
