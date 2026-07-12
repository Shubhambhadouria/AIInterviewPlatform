package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.questionbank.dto.CandidateProfileResponse;
import com.aiinterviewcoach.modules.questionbank.dto.CandidateProjectRequest;
import com.aiinterviewcoach.modules.questionbank.dto.CandidateSkillRequest;
import com.aiinterviewcoach.modules.questionbank.dto.ProjectTechnologyRequest;
import com.aiinterviewcoach.modules.questionbank.dto.UpdateCandidateProfileRequest;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProject;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateSkill;
import com.aiinterviewcoach.modules.questionbank.entity.ProjectTechnology;
import com.aiinterviewcoach.modules.questionbank.enums.CandidateProfileStatus;
import com.aiinterviewcoach.modules.questionbank.enums.SkillSource;
import com.aiinterviewcoach.modules.questionbank.mapper.CandidateProfileMapper;
import com.aiinterviewcoach.modules.questionbank.repository.CandidateProfileRepository;
import com.aiinterviewcoach.modules.questionbank.service.CandidateProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

	private final CandidateProfileRepository candidateProfileRepository;
	private final CandidateProfileMapper candidateProfileMapper;

	@Override
	@Transactional(readOnly = true)
	public CandidateProfileResponse getProfileByResume(UUID resumeId, String userEmail) {

		CandidateProfile profile = getOwnedProfile(resumeId, userEmail);

		return candidateProfileMapper.toResponse(profile);
	}

	@Override
	@Transactional(readOnly = true)
	public CandidateProfileResponse getLatestProfile(String userEmail) {

		CandidateProfile profile = candidateProfileRepository.findFirstByResumeUserEmailOrderByUpdatedAtDesc(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

		return candidateProfileMapper.toResponse(profile);
	}

	@Override
	@Transactional
	public CandidateProfileResponse updateProfile(UUID resumeId, UpdateCandidateProfileRequest request,
			String userEmail) {

		CandidateProfile profile = getOwnedProfile(resumeId, userEmail);

		updateBasicDetails(profile, request);
		replaceSkills(profile, request.skills());
		replaceProjects(profile, request.projects());

		if (profile.getStatus() == CandidateProfileStatus.CONFIRMED) {

			profile.setStatus(CandidateProfileStatus.REVIEW_REQUIRED);

			profile.setConfirmedAt(null);
		}

		CandidateProfile savedProfile = candidateProfileRepository.save(profile);

		return candidateProfileMapper.toResponse(savedProfile);
	}

	@Override
	@Transactional
	public CandidateProfileResponse confirmProfile(UUID resumeId, String userEmail) {

		CandidateProfile profile = getOwnedProfile(resumeId, userEmail);

		validateBeforeConfirmation(profile);

		profile.setStatus(CandidateProfileStatus.CONFIRMED);

		profile.setConfirmedAt(LocalDateTime.now());

		CandidateProfile savedProfile = candidateProfileRepository.save(profile);

		return candidateProfileMapper.toResponse(savedProfile);
	}

	private CandidateProfile getOwnedProfile(UUID resumeId, String userEmail) {

		return candidateProfileRepository.findByResumeIdAndResumeUserEmail(resumeId, userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
	}

	private void updateBasicDetails(CandidateProfile profile, UpdateCandidateProfileRequest request) {

		profile.setFullName(normalize(request.fullName()));

		profile.setProfessionalTitle(normalize(request.professionalTitle()));

		profile.setTotalExperienceMonths(request.totalExperienceMonths());

		profile.setProfessionalSummary(normalize(request.professionalSummary()));

		profile.setCurrentCompany(normalize(request.currentCompany()));

		profile.setCurrentRole(normalize(request.currentRole()));

		profile.setTargetRole(normalize(request.targetRole()));
	}

	private void replaceSkills(CandidateProfile profile, List<CandidateSkillRequest> requests) {

		profile.getSkills().clear();

		if (requests == null || requests.isEmpty()) {
			return;
		}

		for (CandidateSkillRequest request : requests) {

			if (request == null || request.skillName() == null || request.skillName().isBlank()) {
				continue;
			}

			CandidateSkill skill = CandidateSkill.builder().name(request.skillName().trim())
					.category(request.category()).proficiency(request.proficiency())
					.yearsOfExperience(sanitizeExperience(request.yearsOfExperience()))
					.source(request.source() != null ? request.source() : SkillSource.USER_ADDED)
					.evidence(normalize(request.evidence())).verified(request.verified()).build();

			profile.addSkill(skill);
		}
	}

	private BigDecimal sanitizeExperience(BigDecimal yearsOfExperience) {

		if (yearsOfExperience == null) {
			return null;
		}

		if (yearsOfExperience.signum() < 0) {
			throw new BadRequestException("Years of experience cannot be negative");
		}

		if (yearsOfExperience.compareTo(BigDecimal.valueOf(60)) > 0) {
			throw new BadRequestException("Years of experience cannot exceed 60");
		}

		return yearsOfExperience;
	}

	private void replaceProjects(CandidateProfile profile, List<CandidateProjectRequest> requests) {

		profile.getProjects().clear();

		if (requests == null) {
			return;
		}

		for (CandidateProjectRequest request : requests) {
			if (request == null || request.projectName() == null || request.projectName().isBlank()) {
				continue;
			}

			CandidateProject project = CandidateProject.builder().projectName(request.projectName().trim())
					.domain(normalize(request.domain())).projectDescription(normalize(request.projectDescription()))
					.candidateRole(normalize(request.candidateRole()))
					.responsibilities(normalize(request.responsibilities()))
					.businessImpact(normalize(request.businessImpact())).startDate(request.startDate())
					.endDate(request.endDate()).currentProject(Boolean.TRUE.equals(request.currentProject())).build();

			addTechnologies(project, request.technologies());

			profile.addProject(project);
		}
	}

	private void addTechnologies(CandidateProject project, List<ProjectTechnologyRequest> requests) {

		if (requests == null) {
			return;
		}

		for (ProjectTechnologyRequest request : requests) {
			if (request == null || request.technologyName() == null || request.technologyName().isBlank()) {
				continue;
			}

			ProjectTechnology technology = ProjectTechnology.builder().technologyName(request.technologyName().trim())
					.usageDescription(normalize(request.usageDescription()))
					.verified(Boolean.TRUE.equals(request.verified())).build();

			project.addTechnology(technology);
		}
	}

	private void validateBeforeConfirmation(CandidateProfile profile) {

		if (isBlank(profile.getFullName())) {
			throw new BadRequestException("Full name is required before profile confirmation");
		}

		if (isBlank(profile.getProfessionalTitle())) {
			throw new BadRequestException("Professional title is required before profile confirmation");
		}

		if (isBlank(profile.getTargetRole())) {
			throw new BadRequestException("Target role is required before profile confirmation");
		}

		if (profile.getSkills() == null || profile.getSkills().isEmpty()) {

			throw new BadRequestException("At least one skill is required before profile confirmation");
		}
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}

		String normalized = value.trim();

		return normalized.isBlank() ? null : normalized;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
