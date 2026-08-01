package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.dto.StoredFile;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;
import com.aiinterviewcoach.modules.questionbank.enums.ResumeStatus;
import com.aiinterviewcoach.modules.questionbank.repository.CandidateProfileRepository;
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
	private final CandidateProfileRepository candidateProfileRepository;

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

			Resume savedResume = resumeRepository.save(resume);

			CandidateProfile candidateProfile = CandidateProfile.builder().resume(savedResume)
					.fullName(user.getFullName()).email(user.getEmail()).totalExperienceMonths(0)
					.status(CandidateProfileStatus.PROCESSING).build();

			candidateProfileRepository.save(candidateProfile);

			return new ResumeUploadResponse(savedResume.getId(), savedResume.getOriginalFileName(),
					savedResume.getContentType(), savedResume.getFileSize(), savedResume.getStatus(),
					savedResume.getCreatedAt(), savedResume.getParsedAt(), "Resume uploaded successfully.");

		} catch (RuntimeException exception) {
			fileStorageService.delete(storedFile.storageKey());
			throw exception;
		}
	}

	@Override
	@Transactional
	public void delete(UUID resumeId, String userEmail) {
		Resume resume = resumeRepository.findByIdAndUserEmail(resumeId, userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));

		candidateProfileRepository.findByResumeId(resumeId).ifPresent(candidateProfileRepository::delete);

		resumeRepository.delete(resume);
		fileStorageService.delete(resume.getStorageKey());
	}
}
