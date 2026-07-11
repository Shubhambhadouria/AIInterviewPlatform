package com.aiinterviewcoach.modules.questionbank.repository;

import com.aiinterviewcoach.modules.questionbank.entity.ProjectTechnology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectTechnologyRepository extends JpaRepository<ProjectTechnology, UUID> {

	List<ProjectTechnology> findAllByCandidateProjectId(UUID projectId);
}
