package com.aiinterviewcoach.modules.questionbank.service.impl;

import org.springframework.stereotype.Service;

import com.aiinterviewcoach.modules.questionbank.dto.GenerateQuestionBankRequest;
import com.aiinterviewcoach.modules.questionbank.dto.GeneratedQuestionBank;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.service.QuestionBankAiGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiQuestionBankAiGenerator implements QuestionBankAiGenerator {

	@Override
	public GeneratedQuestionBank generate(CandidateProfile profile, GenerateQuestionBankRequest request) {

		// Gemini integration will be added here.
		throw new UnsupportedOperationException("Gemini Question Bank generation is not implemented yet");
	}
}
