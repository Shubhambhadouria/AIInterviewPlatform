package com.aiinterviewcoach.modules.questionbank.service;

import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeParseResponse;

public interface ResumeParsingService {

	ResumeParseResponse parse(UUID resumeId, String userEmail);
}
