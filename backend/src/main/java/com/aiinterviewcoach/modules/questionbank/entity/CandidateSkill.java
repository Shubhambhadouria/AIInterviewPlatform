package com.aiinterviewcoach.modules.questionbank.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "candidate_skills", indexes = {
		@Index(name = "idx_candidate_skill_profile", columnList = "candidate_profile_id"),
		@Index(name = "idx_candidate_skill_name", columnList = "skill_name") })

public class CandidateSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_profile_id", nullable = false)
	private CandidateProfile candidateProfile;

	@Column(name = "skill_name", nullable = false, length = 150)
	private String skillName;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 50)
	private SkillCategory category;

	@Enumerated(EnumType.STRING)
	@Column(name = "proficiency", length = 30)
	private SkillProficiency proficiency;

	@Column(name = "years_of_experience", precision = 4, scale = 1)
	private BigDecimal yearsOfExperience;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false, length = 30)
	private SkillSource source;

	@Column(name = "evidence", columnDefinition = "TEXT")
	private String evidence;

	@Column(name = "verified", nullable = false)
	private boolean verified;

	@PrePersist
	public void prePersist() {

		if (source == null) {
			source = SkillSource.USER_ADDED;
		}
	}
}