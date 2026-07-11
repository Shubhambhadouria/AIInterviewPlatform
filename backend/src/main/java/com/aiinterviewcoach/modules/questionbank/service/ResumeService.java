package com.aiinterviewcoach.modules.questionbank.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;

public interface ResumeService {

	ResumeUploadResponse uploadResume(MultipartFile file, String userEmail);

	List<ResumeResponse> getUserResumes(String userEmail);

	ResumeResponse getResume(UUID resumeId, String userEmail);

	void activateResume(UUID resumeId, String userEmail);

	void deleteResume(UUID resumeId, String userEmail);
}
