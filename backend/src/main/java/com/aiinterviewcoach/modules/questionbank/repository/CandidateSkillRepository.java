package com.aiinterviewcoach.modules.questionbank.repository;

import com.aiinterviewcoach.modules.questionbank.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, UUID> {

	List<CandidateSkill> findAllByCandidateProfileId(UUID profileId);
}