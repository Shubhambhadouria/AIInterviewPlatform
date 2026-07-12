package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.QuestionBankStatus;

public record QuestionBankSummaryResponse(UUID questionBankId, UUID candidateProfileId, String title,
		QuestionBankStatus status, int totalSections, int totalQuestions, String message) {
}
