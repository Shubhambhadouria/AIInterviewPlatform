package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.ProfileStatus;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import com.aiinterviewcoach.modules.questionbank.entity.ResumeStatus;
import com.aiinterviewcoach.modules.questionbank.mapper.ResumeMapper;
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
	private final ResumeMapper resumeMapper;

	@Override
	@Transactional
	public ResumeUploadResponse uploadResume(MultipartFile file, String userEmail) {

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		FileStorageService.StoredFile storedFile = fileStorageService.storeResume(file);

		try {
			deactivateExistingResumes(userEmail);

			Resume resume = Resume.builder().user(user).originalFileName(storedFile.originalFileName())
					.storedFileName(storedFile.storedFileName()).storedFilePath(storedFile.storedFilePath())
					.mimeType(storedFile.mimeType()).fileSize(storedFile.fileSize()).status(ResumeStatus.UPLOADED)
					.active(true).build();

			CandidateProfile candidateProfile = CandidateProfile.builder().resume(resume).user(user)
					.fullName(user.getFullName()).status(ProfileStatus.DRAFT).build();

			resume.setCandidateProfile(candidateProfile);

			Resume savedResume = resumeRepository.save(resume);

			return resumeMapper.toUploadResponse(savedResume);

		} catch (RuntimeException exception) {

			fileStorageService.deleteFile(storedFile.storedFilePath());

			throw exception;
		}
	}

	@Override
	@Transactional
	public List<ResumeResponse> getUserResumes(String userEmail) {

		return resumeRepository.findAllByUserEmailOrderByUploadedAtDesc(userEmail).stream()
				.map(resumeMapper::toResponse).toList();
	}

	@Override
	@Transactional
	public ResumeResponse getResume(UUID resumeId, String userEmail) {

		Resume resume = getOwnedResume(resumeId, userEmail);

		return resumeMapper.toResponse(resume);
	}

	@Override
	@Transactional
	public void activateResume(UUID resumeId, String userEmail) {

		Resume resumeToActivate = getOwnedResume(resumeId, userEmail);

		deactivateExistingResumes(userEmail);

		resumeToActivate.setActive(true);

		resumeRepository.save(resumeToActivate);
	}

	@Override
	@Transactional
	public void deleteResume(UUID resumeId, String userEmail) {

		Resume resume = getOwnedResume(resumeId, userEmail);

		String storedFilePath = resume.getStoredFilePath();

		resumeRepository.delete(resume);

		fileStorageService.deleteFile(storedFilePath);
	}

	private Resume getOwnedResume(UUID resumeId, String userEmail) {

		return resumeRepository.findByIdAndUserEmail(resumeId, userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
	}

	private void deactivateExistingResumes(String userEmail) {

		List<Resume> resumes = resumeRepository.findAllByUserEmailOrderByUploadedAtDesc(userEmail);

		boolean changed = false;

		for (Resume resume : resumes) {

			if (resume.isActive()) {
				resume.setActive(false);
				changed = true;
			}
		}

		if (changed) {
			resumeRepository.saveAll(resumes);
		}
	}
}