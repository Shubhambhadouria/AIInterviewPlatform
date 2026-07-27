package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "candidate_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfile {

	@Id
	@GeneratedValue
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "resume_id", nullable = false, unique = true)
	private Resume resume;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	/*
	 * Example: Java Backend Developer Java Full Stack Developer
	 */
	@Column(name = "professional_title")
	private String professionalTitle;

	/*
	 * Current designation extracted from the resume.
	 *
	 * Example: Software Engineer
	 */
	@Column(name = "current_job_role")
	private String currentRole;

	/*
	 * Role for which the user wants to prepare.
	 *
	 * Example: Senior Java Backend Developer
	 */
	@Column(name = "target_role")
	private String targetRole;

	@Column(name = "current_company")
	private String currentCompany;

	@Column(name = "total_experience_months")
	private Integer totalExperienceMonths;

	@Column(name = "professional_summary", columnDefinition = "TEXT")
	private String professionalSummary;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private CandidateProfileStatus status;

	/*
	 * Populated when the user confirms the parsed profile.
	 */
	@Column(name = "confirmed_at")
	private LocalDateTime confirmedAt;

	@OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CandidateSkill> skills = new ArrayList<>();

	@OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CandidateProject> projects = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public void addSkill(CandidateSkill skill) {
		if (skill == null) {
			return;
		}

		skills.add(skill);
		skill.setCandidateProfile(this);
	}

	public void removeSkill(CandidateSkill skill) {
		if (skill == null) {
			return;
		}

		skills.remove(skill);
		skill.setCandidateProfile(null);
	}

	public void addProject(CandidateProject project) {
		if (project == null) {
			return;
		}

		projects.add(project);
		project.setCandidateProfile(this);
	}

	public void removeProject(CandidateProject project) {
		if (project == null) {
			return;
		}

		projects.remove(project);
		project.setCandidateProfile(null);
	}

	public void clearParsedInformation() {
		skills.clear();
		projects.clear();
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();

		createdAt = now;
		updatedAt = now;

		if (status == null) {
			status = CandidateProfileStatus.PROCESSING;
		}
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
