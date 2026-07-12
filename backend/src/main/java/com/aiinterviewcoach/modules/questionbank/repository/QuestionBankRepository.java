package com.aiinterviewcoach.modules.questionbank.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiinterviewcoach.modules.questionbank.entity.QuestionBank;
public interface QuestionBankRepository extends JpaRepository<QuestionBank, UUID> {
    Optional<QuestionBank> findByIdAndUserEmail(UUID questionBankId, String email);
    List<QuestionBank> findAllByUserEmailOrderByCreatedAtDesc(String email);
}

