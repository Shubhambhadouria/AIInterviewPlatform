package com.aiinterviewcoach.modules.questionbank.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {

	StoredFile storeResume(MultipartFile file);

	void deleteFile(String storedFilePath);

	Path resolveFile(String storedFilePath);

	record StoredFile(String originalFileName, String storedFileName, String storedFilePath, String mimeType,
			long fileSize) {
	}
}
