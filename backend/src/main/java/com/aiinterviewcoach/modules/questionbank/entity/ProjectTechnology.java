package com.aiinterviewcoach.modules.questionbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "project_technologies", indexes = {
		@Index(name = "idx_project_technology_project", columnList = "candidate_project_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTechnology {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_project_id", nullable = false)
	private CandidateProject candidateProject;

	@Column(name = "technology_name", nullable = false, length = 150)
	private String technologyName;

	@Column(name = "usage_description", columnDefinition = "TEXT")
	private String usageDescription;

	@Column(name = "verified", nullable = false)
	private boolean verified;
}
