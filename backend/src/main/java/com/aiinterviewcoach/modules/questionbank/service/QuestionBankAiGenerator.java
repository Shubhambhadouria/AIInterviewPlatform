package com.aiinterviewcoach.modules.questionbank.service;

import com.aiinterviewcoach.modules.questionbank.dto.GenerateQuestionBankRequest;
import com.aiinterviewcoach.modules.questionbank.dto.GeneratedQuestionBank;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;

public interface QuestionBankAiGenerator {
	GeneratedQuestionBank generate(CandidateProfile profile, GenerateQuestionBankRequest request);
}
