package com.aiinterviewcoach.modules.questionbank.service.impl;

import com.aiinterviewcoach.modules.questionbank.config.FileStorageProperties;
import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageServiceImpl implements FileStorageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx");

	private static final Set<String> ALLOWED_MIME_TYPES = Set.of("application/pdf",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

	private final FileStorageProperties fileStorageProperties;

	@Override
	public StoredFile storeResume(MultipartFile file) {

		validateFile(file);

		String originalFileName = sanitizeFileName(file.getOriginalFilename());

		String extension = getExtension(originalFileName);

		String storedFileName = UUID.randomUUID() + "." + extension;

		Path uploadDirectory = fileStorageProperties.getUploadPath();

		try {
			Files.createDirectories(uploadDirectory);

			Path targetPath = uploadDirectory.resolve(storedFileName).normalize();

			if (!targetPath.startsWith(uploadDirectory)) {
				throw new FileStorageException("Invalid file storage path");
			}

			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

			return new StoredFile(originalFileName, storedFileName, targetPath.toString(), file.getContentType(),
					file.getSize());

		} catch (IOException exception) {
			throw new FileStorageException("Unable to store resume file", exception);
		}
	}

	@Override
	public void deleteFile(String storedFilePath) {

		if (storedFilePath == null || storedFilePath.isBlank()) {
			return;
		}

		try {
			Files.deleteIfExists(Path.of(storedFilePath));
		} catch (IOException exception) {
			throw new FileStorageException("Unable to delete stored resume file", exception);
		}
	}

	@Override
	public Path resolveFile(String storedFilePath) {

		if (storedFilePath == null || storedFilePath.isBlank()) {
			throw new FileStorageException("Stored file path is missing");
		}

		Path filePath = Path.of(storedFilePath).toAbsolutePath().normalize();

		if (!Files.exists(filePath)) {
			throw new FileStorageException("Resume file does not exist");
		}

		return filePath;
	}

	private void validateFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new FileStorageException("Resume file is required");
		}

		if (file.getSize() > fileStorageProperties.getMaximumFileSize()) {

			throw new FileStorageException("Resume file exceeds the maximum allowed size");
		}

		String originalFileName = sanitizeFileName(file.getOriginalFilename());

		String extension = getExtension(originalFileName);

		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new FileStorageException("Only PDF and DOCX resume files are allowed");
		}

		String mimeType = file.getContentType();

		if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {

			throw new FileStorageException("Unsupported resume file type");
		}
	}

	private String sanitizeFileName(String originalFileName) {

		String cleanedFileName = StringUtils.cleanPath(originalFileName == null ? "" : originalFileName);

		if (cleanedFileName.isBlank()) {
			throw new FileStorageException("Resume file name is missing");
		}

		if (cleanedFileName.contains("..")) {
			throw new FileStorageException("Invalid resume file name");
		}

		return cleanedFileName;
	}

	private String getExtension(String fileName) {

		int index = fileName.lastIndexOf('.');

		if (index < 0 || index == fileName.length() - 1) {

			throw new FileStorageException("Resume file extension is missing");
		}

		return fileName.substring(index + 1).toLowerCase(Locale.ROOT);
	}
}