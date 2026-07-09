package com.aiinterviewcoach.modules.interview.dto;

import java.util.UUID;

public record QuestionResult(
        UUID questionId,
        String question,
        String answer,
        Integer score,
        String feedback) {
}
