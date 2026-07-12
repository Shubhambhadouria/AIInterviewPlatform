package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.QuestionBankStatus;
import com.aiinterviewcoach.modules.user.entity.User;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_banks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBank {
	@Id
	@GeneratedValue
	private UUID id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_profile_id", nullable = false)
	private CandidateProfile candidateProfile;
	@Column(name = "title", nullable = false)
	private String title;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private QuestionBankStatus status;
	@Column(name = "generation_error", columnDefinition = "TEXT")
	private String generationError;
	@Column(name = "generated_at")
	private LocalDateTime generatedAt;
	@OneToMany(mappedBy = "questionBank", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<QuestionSection> sections = new ArrayList<>();
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public void addSection(QuestionSection section) {
		sections.add(section);
		section.setQuestionBank(this);
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (status == null)
			status = QuestionBankStatus.GENERATING;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
