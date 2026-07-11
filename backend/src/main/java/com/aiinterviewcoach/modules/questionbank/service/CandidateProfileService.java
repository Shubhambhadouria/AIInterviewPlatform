package com.aiinterviewcoach.modules.questionbank.service;

import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.dto.CandidateProfileResponse;
import com.aiinterviewcoach.modules.questionbank.dto.UpdateCandidateProfileRequest;

public interface CandidateProfileService {

	CandidateProfileResponse getProfileByResume(UUID resumeId, String userEmail);

	CandidateProfileResponse getLatestProfile(String userEmail);

	CandidateProfileResponse updateProfile(UUID resumeId, UpdateCandidateProfileRequest request, String userEmail);

	CandidateProfileResponse confirmProfile(UUID resumeId, String userEmail);
}
