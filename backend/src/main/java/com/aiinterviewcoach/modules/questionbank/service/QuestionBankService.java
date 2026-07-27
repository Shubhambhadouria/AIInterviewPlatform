package com.aiinterviewcoach.modules.questionbank.service;

import com.aiinterviewcoach.modules.questionbank.dto.GenerateQuestionBankRequest;
import com.aiinterviewcoach.modules.questionbank.dto.QuestionBankSummaryResponse;

public interface QuestionBankService {
	QuestionBankSummaryResponse generate(GenerateQuestionBankRequest request, String userEmail);
}
