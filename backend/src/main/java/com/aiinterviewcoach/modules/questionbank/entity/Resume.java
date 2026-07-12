package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;
import com.aiinterviewcoach.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "original_file_name", nullable = false)
	private String originalFileName;

	@Column(name = "stored_file_name", nullable = false)
	private String storedFileName;

	/*
	 * This is the relative or absolute location used by FileStorageService.
	 *
	 * Example: resumes/user-id/generated-file-name.pdf
	 */
	@Column(name = "storage_key", nullable = false, unique = true)
	private String storageKey;

	@Column(name = "content_type", nullable = false)
	private String contentType;

	@Column(name = "file_size", nullable = false)
	private Long fileSize;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ResumeStatus status;

	@Column(name = "parsing_error", columnDefinition = "TEXT")
	private String parsingError;

	@Column(name = "parsed_at")
	private LocalDateTime parsedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();

		createdAt = now;
		updatedAt = now;

		if (status == null) {
			status = ResumeStatus.UPLOADED;
		}
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}