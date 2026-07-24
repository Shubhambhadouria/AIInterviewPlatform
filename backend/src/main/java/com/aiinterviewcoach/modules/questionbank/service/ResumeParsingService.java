package com.aiinterviewcoach.modules.questionbank.service;

import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeParseResponse;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;

public interface ResumeParsingService {

	ResumeParseResponse parse(UUID resumeId, String userEmail);
	ResumeParseResponse parseResume(UUID resumeId, String userEmail);
	CandidateProfile parse(Resume resume);
}
