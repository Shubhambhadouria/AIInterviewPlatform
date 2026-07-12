package com.aiinterviewcoach.modules.questionbank.dto;

public record StoredFile(String originalFileName, String storedFileName, String storageKey, String contentType,
		long fileSize) {
}
