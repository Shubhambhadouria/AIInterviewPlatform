package com.aiinterviewcoach.modules.questionbank.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;

public record ResumeUploadResponse(

		UUID resumeId,

		String originalFileName,

		String contentType,

		Long fileSize,

		ResumeStatus status,

		LocalDateTime uploadedAt,

		LocalDateTime parsedAt,
		
		String message

) {
}
