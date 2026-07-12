package com.aiinterviewcoach.modules.questionbank.mapper;

import org.springframework.stereotype.Component;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;

@Component
public class ResumeMapper {

	public ResumeUploadResponse toUploadResponse(Resume resume) {

		return new ResumeUploadResponse(resume.getId(), resume.getOriginalFileName(), resume.getContentType(),
				resume.getFileSize(), resume.getStatus(), resume.getCreatedAt(),resume.getParsedAt(), "Resume uploaded successfully");
	}

	public ResumeResponse toResponse(Resume resume) {

		return new ResumeResponse(resume.getId(), resume.getOriginalFileName(), resume.getContentType(),
				resume.getFileSize(), resume.getStatus(), resume.getCreatedAt(), resume.getParsedAt());
	}
}