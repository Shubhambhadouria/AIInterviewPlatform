package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.modules.questionbank.dto.StoredFile;
import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;

import jakarta.annotation.PostConstruct;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

	@Value("${app.file-storage.resume-directory}")
	private String resumeDirectory;

	private Path rootDirectory;

	@PostConstruct
	void initialize() {
		try {
			rootDirectory = Path.of(resumeDirectory).toAbsolutePath().normalize();
			Files.createDirectories(rootDirectory);
		} catch (IOException exception) {
			throw new FileStorageException("Unable to initialize resume storage directory", exception);
		}
	}

	@Override
	public StoredFile store(MultipartFile file, UUID userId) {
		validateFile(file);
		String originalFileName = sanitizeFileName(file.getOriginalFilename());
		String extension = getFileExtension(originalFileName);
		String storedFileName = UUID.randomUUID() + extension;
		String storageKey = "resumes/" + userId + "/" + storedFileName;
		Path targetPath = resolveStoragePath(storageKey);

		try {
			Files.createDirectories(targetPath.getParent());
			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			return new StoredFile(originalFileName, storedFileName, storageKey, file.getContentType(), file.getSize());
		} catch (IOException exception) {
			throw new FileStorageException("Failed to store resume file", exception);
		}
	}

	@Override
	public byte[] read(String storageKey) {
		Path filePath = resolveStoragePath(storageKey);
		if (!Files.exists(filePath)) {
			throw new FileStorageException("Stored resume file does not exist");
		}
		try {
			return Files.readAllBytes(filePath);
		} catch (IOException exception) {
			throw new FileStorageException("Failed to read stored resume", exception);
		}
	}

	@Override
	public void delete(String storageKey) {
		try {
			Files.deleteIfExists(resolveStoragePath(storageKey));
		} catch (IOException exception) {
			throw new FileStorageException("Failed to delete stored resume", exception);
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty())
			throw new BadRequestException("Resume file must not be empty");
		if (file.getSize() > MAX_FILE_SIZE)
			throw new BadRequestException("Resume file must not exceed 10 MB");
		if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType()))
			throw new BadRequestException("Only PDF and DOCX resume files are supported");
	}

	private Path resolveStoragePath(String storageKey) {
		Path resolvedPath = rootDirectory.resolve(storageKey).normalize();
		if (!resolvedPath.startsWith(rootDirectory))
			throw new FileStorageException("Invalid file storage path");
		return resolvedPath;
	}

	private String sanitizeFileName(String fileName) {
		if (fileName == null || fileName.isBlank())
			return "resume";
		return Path.of(fileName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
	}

	private String getFileExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		return lastDot < 0 ? "" : fileName.substring(lastDot).toLowerCase();
	}
}
