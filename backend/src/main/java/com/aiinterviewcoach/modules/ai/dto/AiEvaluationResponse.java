package com.aiinterviewcoach.modules.ai.dto;

public record AiEvaluationResponse(
		Integer score,
        String feedback,
        String improvedAnswer) {
}
