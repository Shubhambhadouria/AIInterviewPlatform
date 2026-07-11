package com.aiinterviewcoach.modules.questionbank.entity;

import com.aiinterviewcoach.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resumes", indexes = { @Index(name = "idx_resume_user", columnList = "user_id"),
		@Index(name = "idx_resume_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "original_file_name", nullable = false, length = 255)
	private String originalFileName;

	@Column(name = "stored_file_name", nullable = false, unique = true, length = 255)
	private String storedFileName;

	@Column(name = "stored_file_path", nullable = false, length = 1000)
	private String storedFilePath;

	@Column(name = "mime_type", nullable = false, length = 150)
	private String mimeType;

	@Column(name = "file_size", nullable = false)
	private Long fileSize;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private ResumeStatus status;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Column(name = "uploaded_at", nullable = false, updatable = false)
	private LocalDateTime uploadedAt;

	@Column(name = "parsed_at")
	private LocalDateTime parsedAt;

	@Column(name = "parsing_error", columnDefinition = "TEXT")
	private String parsingError;

	@OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private CandidateProfile candidateProfile;

	@PrePersist
	public void prePersist() {

		if (uploadedAt == null) {
			uploadedAt = LocalDateTime.now();
		}

		if (status == null) {
			status = ResumeStatus.UPLOADED;
		}

		active = true;
	}
}