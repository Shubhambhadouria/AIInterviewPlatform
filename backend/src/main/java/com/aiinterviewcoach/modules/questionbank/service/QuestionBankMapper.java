package com.aiinterviewcoach.modules.questionbank.service;

import com.aiinterviewcoach.modules.questionbank.dto.GeneratedQuestionBank;
import com.aiinterviewcoach.modules.questionbank.entity.QuestionBank;

public interface QuestionBankMapper {
	void mapGeneratedContent(
			QuestionBank questionBank,
			GeneratedQuestionBank generatedQuestionBank
	);
}
