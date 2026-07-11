package com.aiinterviewcoach.modules.questionbank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {

	private String uploadDirectory = "uploads/resumes";

	private long maximumFileSize = 10 * 1024 * 1024;

	public String getUploadDirectory() {
		return uploadDirectory;
	}

	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	public long getMaximumFileSize() {
		return maximumFileSize;
	}

	public void setMaximumFileSize(long maximumFileSize) {
		this.maximumFileSize = maximumFileSize;
	}

	public Path getUploadPath() {
		return Path.of(uploadDirectory).toAbsolutePath().normalize();
	}
}
