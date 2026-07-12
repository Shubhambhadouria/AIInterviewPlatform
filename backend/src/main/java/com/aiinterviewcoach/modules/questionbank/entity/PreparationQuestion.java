package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.PreparationProgressStatus;
import com.aiinterviewcoach.modules.questionbank.enums.QuestionDifficulty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "preparation_questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreparationQuestion {
	@Id
	@GeneratedValue
	private UUID id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "section_id", nullable = false)
	private QuestionSection section;
	@Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
	private String questionText;
	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty", nullable = false)
	private QuestionDifficulty difficulty;
	@Column(name = "question_type")
	private String questionType;
	@Column(name = "source_skill")
	private String sourceSkill;
	@Column(name = "source_project")
	private String sourceProject;
	@Column(name = "resume_relevance", columnDefinition = "TEXT")
	private String resumeRelevance;
	@Column(name = "bookmarked", nullable = false)
	private Boolean bookmarked;
	@Enumerated(EnumType.STRING)
	@Column(name = "progress_status", nullable = false)
	private PreparationProgressStatus progressStatus;
	@OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private PreparationAnswer answer;
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public void setAnswer(PreparationAnswer answer) {
		this.answer = answer;
		if (answer != null)
			answer.setQuestion(this);
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (bookmarked == null)
			bookmarked = false;
		if (progressStatus == null)
			progressStatus = PreparationProgressStatus.NOT_STARTED;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
