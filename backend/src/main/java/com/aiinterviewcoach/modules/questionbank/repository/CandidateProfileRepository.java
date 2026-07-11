package com.aiinterviewcoach.modules.questionbank.repository;

import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {

	Optional<CandidateProfile> findByResumeIdAndUserEmail(UUID resumeId, String userEmail);

	Optional<CandidateProfile> findByIdAndUserEmail(UUID profileId, String userEmail);

	Optional<CandidateProfile> findFirstByUserEmailOrderByUpdatedAtDesc(String userEmail);
}
