package com.aiinterviewcoach.modules.questionbank.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.aiinterviewcoach.modules.questionbank.entity.PreparationQuestion;

public interface PreparationQuestionRepository extends JpaRepository<PreparationQuestion, UUID> {
	Optional<PreparationQuestion> findByIdAndSectionQuestionBankUserEmail(UUID questionId, String email);
}
