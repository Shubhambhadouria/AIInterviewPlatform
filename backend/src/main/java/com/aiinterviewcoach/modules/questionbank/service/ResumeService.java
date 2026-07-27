package com.aiinterviewcoach.modules.questionbank.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;

public interface ResumeService {

	ResumeUploadResponse upload(MultipartFile file, String userEmail);

	void delete(UUID resumeId, String userEmail);

}
