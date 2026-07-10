package com.aiinterviewcoach.modules.interview.dto;

import java.util.UUID;

public record SubmitAnswerResponse(
		UUID questionId,
        Integer score,
        String feedback,
        String message) {
}
