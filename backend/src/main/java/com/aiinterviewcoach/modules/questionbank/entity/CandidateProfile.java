package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aiinterviewcoach.modules.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_profiles", uniqueConstraints = {
		@UniqueConstraint(name = "uk_candidate_profile_resume", columnNames = "resume_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "resume_id", nullable = false, unique = true)
	private Resume resume;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "full_name", length = 150)
	private String fullName;

	@Column(name = "professional_title", length = 200)
	private String professionalTitle;

	@Column(name = "total_experience_months")
	private Integer totalExperienceMonths;

	@Column(name = "professional_summary", columnDefinition = "TEXT")
	private String professionalSummary;

	@Column(name = "current_company", length = 200)
	private String currentCompany;

	@Column(name = "current_job_role", length = 200)
	private String currentRole;

	@Column(name = "target_role", length = 200)
	private String targetRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private ProfileStatus status;

	@Column(name = "confirmed_at")
	private LocalDateTime confirmedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CandidateSkill> skills = new ArrayList<>();

	@OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CandidateProject> projects = new ArrayList<>();

	public void addSkill(CandidateSkill skill) {
		skills.add(skill);
		skill.setCandidateProfile(this);
	}

	public void removeSkill(CandidateSkill skill) {
		skills.remove(skill);
		skill.setCandidateProfile(null);
	}

	public void addProject(CandidateProject project) {
		projects.add(project);
		project.setCandidateProfile(this);
	}

	public void removeProject(CandidateProject project) {
		projects.remove(project);
		project.setCandidateProfile(null);
	}

	@PrePersist
	public void prePersist() {

		LocalDateTime now = LocalDateTime.now();

		if (createdAt == null) {
			createdAt = now;
		}

		updatedAt = now;

		if (status == null) {
			status = ProfileStatus.DRAFT;
		}
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
