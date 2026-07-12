package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "preparation_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreparationAnswer {
	@Id
	@GeneratedValue
	private UUID id;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "question_id", nullable = false, unique = true)
	private PreparationQuestion question;
	@Column(name = "short_answer", columnDefinition = "TEXT")
	private String shortAnswer;
	@Column(name = "detailed_answer", columnDefinition = "TEXT")
	private String detailedAnswer;
	@Column(name = "internal_working", columnDefinition = "TEXT")
	private String internalWorking;
	@Column(name = "code_example", columnDefinition = "TEXT")
	private String codeExample;
	@Column(name = "project_usage", columnDefinition = "TEXT")
	private String projectUsage;
	@Column(name = "star_answer", columnDefinition = "TEXT")
	private String starAnswer;
	@Column(name = "business_impact", columnDefinition = "TEXT")
	private String businessImpact;
	@Column(name = "follow_up_questions", columnDefinition = "TEXT")
	private String followUpQuestions;
	@Column(name = "common_mistakes", columnDefinition = "TEXT")
	private String commonMistakes;
	@Column(name = "what_not_to_say", columnDefinition = "TEXT")
	private String whatNotToSay;
	@Column(name = "product_company_expectation", columnDefinition = "TEXT")
	private String productCompanyExpectation;
	@Column(name = "service_company_expectation", columnDefinition = "TEXT")
	private String serviceCompanyExpectation;
	@Column(name = "last_user_feedback", columnDefinition = "TEXT")
	private String lastUserFeedback;
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
