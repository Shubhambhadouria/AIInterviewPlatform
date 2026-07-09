package com.aiinterviewcoach.modules.ai.service;

import com.aiinterviewcoach.modules.ai.dto.AiEvaluationResponse;

public interface AiEvaluationService {
	AiEvaluationResponse evaluateAnswer(
            String question,
            String userAnswer,
            String topic,
            String targetRole,
            String experienceLevel
    );
}
