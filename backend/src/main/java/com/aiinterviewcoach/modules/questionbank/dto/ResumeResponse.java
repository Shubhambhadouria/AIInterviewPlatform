package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.entity.ResumeStatus;

public record ResumeResponse(

		UUID id, String originalFileName, String mimeType, Long fileSize, ResumeStatus status, boolean active,
		LocalDateTime uploadedAt, LocalDateTime parsedAt, UUID candidateProfileId

) {
}
