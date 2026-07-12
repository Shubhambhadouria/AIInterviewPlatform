package com.aiinterviewcoach.modules.questionbank.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.SkillCategory;
import com.aiinterviewcoach.modules.questionbank.enums.SkillProficiency;
import com.aiinterviewcoach.modules.questionbank.enums.SkillSource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_skills")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSkill {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_profile_id", nullable = false)
	private CandidateProfile candidateProfile;

	@Column(name = "skill_name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private SkillCategory category;

	@Enumerated(EnumType.STRING)
	@Column(name = "proficiency")
	private SkillProficiency proficiency;

	@Column(name = "years_of_experience", precision = 5, scale = 2)
	private BigDecimal yearsOfExperience;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false)
	private SkillSource source;

	@Column(name = "evidence", columnDefinition = "TEXT")
	private String evidence;

	@Column(name = "verified", nullable = false)
	private Boolean verified;
}