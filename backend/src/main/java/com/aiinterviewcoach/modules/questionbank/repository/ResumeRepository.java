package com.aiinterviewcoach.modules.questionbank.repository;

import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

	List<Resume> findAllByUserEmailOrderByUploadedAtDesc(String userEmail);

	Optional<Resume> findByIdAndUserEmail(UUID resumeId, String userEmail);

	Optional<Resume> findFirstByUserEmailAndActiveTrueOrderByUploadedAtDesc(String userEmail);

	boolean existsByIdAndUserEmail(UUID resumeId, String userEmail);
}
