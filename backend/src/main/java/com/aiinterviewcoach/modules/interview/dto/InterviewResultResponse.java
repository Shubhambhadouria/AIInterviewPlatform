package com.aiinterviewcoach.modules.interview.dto;

import java.util.List;
import java.util.UUID;

public record InterviewResultResponse(
        UUID sessionId,
        String title,
        String topic,
        String targetRole,
        String experienceLevel,
        String status,
        Integer totalQuestions,
        Integer answeredQuestions,
        Integer overallScore,
        String aiSummaryFeedback,
        List<QuestionResult> questions) {
}
