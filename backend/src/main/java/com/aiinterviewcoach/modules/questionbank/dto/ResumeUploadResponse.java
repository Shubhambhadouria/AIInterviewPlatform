package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.entity.ResumeStatus;

public record ResumeUploadResponse(

		UUID resumeId, UUID candidateProfileId, String originalFileName, String mimeType, Long fileSize,
		ResumeStatus status, LocalDateTime uploadedAt, String message

) {
}
