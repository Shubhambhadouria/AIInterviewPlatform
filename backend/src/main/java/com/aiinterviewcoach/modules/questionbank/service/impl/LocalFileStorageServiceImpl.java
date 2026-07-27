package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.StoredFile;
import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

	private final Path uploadDirectory;

	public LocalFileStorageServiceImpl(@Value("${app.file.upload-dir:uploads/resumes}") String uploadDir) {

		this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.uploadDirectory);
		} catch (IOException exception) {
			throw new FileStorageException("Could not create upload directory: " + this.uploadDirectory, exception);
		}
	}

	@Override
	public StoredFile store(MultipartFile file) {
		validateFile(file);

		try {
			String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("resume");

			String extension = getExtension(originalFilename);
			String storedFilename = UUID.randomUUID() + extension;

			Path destination = uploadDirectory.resolve(storedFilename).normalize();

			validateDestination(destination, uploadDirectory);

			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

			return new StoredFile(storedFilename, originalFilename, file.getContentType(), file.getSize());

		} catch (IOException exception) {
			throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), exception);
		}
	}

	@Override
	public StoredFile store(MultipartFile file, UUID userId) {
		validateFile(file);

		if (userId == null) {
			throw new FileStorageException("User ID cannot be null");
		}

		try {
			Path userUploadDirectory = uploadDirectory.resolve(userId.toString()).normalize();

			validateDestination(userUploadDirectory, uploadDirectory);

			Files.createDirectories(userUploadDirectory);

			String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("resume");

			String extension = getExtension(originalFilename);
			String storedFilename = UUID.randomUUID() + extension;

			Path destination = userUploadDirectory.resolve(storedFilename).normalize();

			validateDestination(destination, userUploadDirectory);

			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

			String storageKey = userId + "/" + storedFilename;

			return new StoredFile(storageKey, originalFilename, file.getContentType(), file.getSize());

		} catch (IOException exception) {
			throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), exception);
		}
	}

	@Override
	public byte[] read(String storageKey) {
		Path filePath = resolveStoragePath(storageKey);

		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new FileStorageException("File not found for storage key: " + storageKey);
		}

		try {
			return Files.readAllBytes(filePath);
		} catch (IOException exception) {
			throw new FileStorageException("Failed to read file: " + storageKey, exception);
		}
	}

	@Override
	public void delete(String storageKey) {
		Path filePath = resolveStoragePath(storageKey);

		try {
			boolean deleted = Files.deleteIfExists(filePath);

			if (!deleted) {
				throw new FileStorageException("File not found for storage key: " + storageKey);
			}
		} catch (IOException exception) {
			throw new FileStorageException("Failed to delete file: " + storageKey, exception);
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new FileStorageException("Resume file cannot be empty");
		}

		String contentType = file.getContentType();

		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new FileStorageException("Only PDF, DOC, and DOCX files are supported");
		}
	}

	private String getExtension(String filename) {
		int dotIndex = filename.lastIndexOf('.');

		if (dotIndex == -1) {
			return "";
		}

		return filename.substring(dotIndex).toLowerCase();
	}

	private Path resolveStoragePath(String storageKey) {
		if (storageKey == null || storageKey.isBlank()) {
			throw new FileStorageException("Storage key cannot be empty");
		}

		Path filePath = uploadDirectory.resolve(storageKey).normalize();

		validateDestination(filePath, uploadDirectory);

		return filePath;
	}

	private void validateDestination(Path destination, Path parentDirectory) {
		if (!destination.startsWith(parentDirectory)) {
			throw new FileStorageException("Invalid file storage path");
		}
	}
}