package com.aiinterviewcoach.modules.questionbank.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProject {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_profile_id", nullable = false)
	private CandidateProfile candidateProfile;

	@Column(name = "project_name", nullable = false)
	private String projectName;

	@Column(name = "domain")
	private String domain;

	@Column(name = "project_description", columnDefinition = "TEXT")
	private String projectDescription;

	@Column(name = "candidate_role")
	private String candidateRole;

	@Column(name = "responsibilities", columnDefinition = "TEXT")
	private String responsibilities;

	@Column(name = "business_impact", columnDefinition = "TEXT")
	private String businessImpact;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "current_project", nullable = false)
	private Boolean currentProject;

	@OneToMany(mappedBy = "candidateProject", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProjectTechnology> technologies = new ArrayList<>();

	public void addTechnology(ProjectTechnology technology) {

		if (technology == null) {
			return;
		}

		technologies.add(technology);
		technology.setCandidateProject(this);
	}
}
