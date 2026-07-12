package com.aiinterviewcoach.modules.questionbank.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {

	Optional<CandidateProfile> findByResumeId(UUID resumeId);

	Optional<CandidateProfile> findByResumeIdAndResumeUserEmail(UUID resumeId, String email);

	Optional<CandidateProfile> findFirstByResumeUserEmailOrderByUpdatedAtDesc(String email);

	Optional<CandidateProfile> findByIdAndResumeUserEmail(UUID profileId, String email);
}
