package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.dto.StoredFile;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;
import com.aiinterviewcoach.modules.questionbank.repository.ResumeRepository;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;
import com.aiinterviewcoach.modules.questionbank.service.ResumeService;
import com.aiinterviewcoach.modules.user.entity.User;
import com.aiinterviewcoach.modules.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

	private final ResumeRepository resumeRepository;
	private final UserRepository userRepository;
	private final FileStorageService fileStorageService;

	@Override
	@Transactional
	public ResumeUploadResponse upload(MultipartFile file, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

		StoredFile storedFile = fileStorageService.store(file, user.getId());
		try {
			Resume resume = Resume.builder().user(user).originalFileName(storedFile.originalFilename())
					.storedFileName(storedFile.originalFilename()).storageKey(storedFile.storageKey())
					.contentType(storedFile.contentType()).fileSize(storedFile.size()).status(ResumeStatus.UPLOADED)
					.build();
			Resume saved = resumeRepository.save(resume);
			return new ResumeUploadResponse(saved.getId(), saved.getOriginalFileName(), saved.getContentType(),
					saved.getFileSize(), saved.getStatus(), saved.getCreatedAt(), saved.getParsedAt(), "Resume uploaded successfully.");
		} catch (RuntimeException ex) {
			fileStorageService.delete(storedFile.storageKey());
			throw ex;
		}
	}

	@Override
	@Transactional
	public void delete(UUID resumeId, String userEmail) {
		Resume resume = resumeRepository.findByIdAndUserEmail(resumeId, userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
		resumeRepository.delete(resume);
		fileStorageService.delete(resume.getStorageKey());
	}
}
