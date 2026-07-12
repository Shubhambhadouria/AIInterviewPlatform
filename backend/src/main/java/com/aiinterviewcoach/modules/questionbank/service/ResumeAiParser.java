package com.aiinterviewcoach.modules.questionbank.service;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeParseResult;

public interface ResumeAiParser {
	ResumeParseResult parse(String resumeText);
}
