package com.aiinterviewcoach.modules.questionbank.service;

import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;

public interface ResumeAiParser {
	CandidateProfile parse(byte[] fileContent, String contentType);
}
