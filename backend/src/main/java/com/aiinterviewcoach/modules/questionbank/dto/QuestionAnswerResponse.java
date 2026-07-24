package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.UUID;

public record QuestionAnswerResponse(UUID questionId, String category, String questionText, String shortAnswer,
		String detailedAnswer) {
}
