package com.aiinterviewcoach.modules.questionbank.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "project_technologies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTechnology {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "candidate_project_id", nullable = false)
	private CandidateProject candidateProject;

	@Column(name = "technology_name", nullable = false)
	private String technologyName;

	@Column(name = "usage_description", columnDefinition = "TEXT")
	private String usageDescription;

	@Column(name = "verified", nullable = false)
	private Boolean verified;
}
