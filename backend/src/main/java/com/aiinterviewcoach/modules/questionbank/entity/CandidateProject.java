package com.aiinterviewcoach.modules.questionbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "candidate_projects", indexes = {
		@Index(name = "idx_candidate_project_profile", columnList = "candidate_profile_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProject {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_profile_id", nullable = false)
	private CandidateProfile candidateProfile;

	@Column(name = "project_name", nullable = false, length = 200)
	private String projectName;

	@Column(name = "domain", length = 200)
	private String domain;

	@Column(name = "project_description", columnDefinition = "TEXT")
	private String projectDescription;

	@Column(name = "candidate_role", length = 200)
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
	private boolean currentProject;

	@OneToMany(mappedBy = "candidateProject", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProjectTechnology> technologies = new ArrayList<>();

	public void addTechnology(ProjectTechnology technology) {
		technologies.add(technology);
		technology.setCandidateProject(this);
	}

	public void removeTechnology(ProjectTechnology technology) {
		technologies.remove(technology);
		technology.setCandidateProject(null);
	}
}
