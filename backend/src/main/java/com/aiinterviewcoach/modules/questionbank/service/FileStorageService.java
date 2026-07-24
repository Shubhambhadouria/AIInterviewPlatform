package com.aiinterviewcoach.modules.questionbank.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.StoredFile;

public interface FileStorageService {

	StoredFile store(MultipartFile file, UUID userId);

	byte[] read(String storageKey);

	void delete(String storageKey);

	StoredFile store(MultipartFile file);

}
