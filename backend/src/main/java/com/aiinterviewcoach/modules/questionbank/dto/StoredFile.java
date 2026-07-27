package com.aiinterviewcoach.modules.questionbank.dto;

public record StoredFile(String storageKey,
        String originalFilename,
        String contentType,
        long size) {
}
