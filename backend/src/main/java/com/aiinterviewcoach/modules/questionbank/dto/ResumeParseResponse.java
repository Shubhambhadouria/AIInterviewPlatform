package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;
import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;

public record ResumeParseResponse(
        UUID resumeId,
        UUID candidateProfileId,
        ResumeStatus status,
        LocalDateTime parsedAt,
        String message
) {
}
