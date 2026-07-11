package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
import com.aiinterviewcoach.modules.questionbank.entity.ProfileStatus;
import com.aiinterviewcoach.modules.questionbank.entity.ProjectTechnology;
import com.aiinterviewcoach.modules.questionbank.entity.SkillSource;
import com.aiinterviewcoach.modules.questionbank.mapper.CandidateProfileMapper;
import com.aiinterviewcoach.modules.questionbank.repository.CandidateProfileRepository;
import com.aiinterviewcoach.modules.questionbank.service.CandidateProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

	private final CandidateProfileRepository candidateProfileRepository;

	private final CandidateProfileMapper candidateProfileMapper;

	@Override
	@Transactional
	public CandidateProfileResponse getProfileByResume(UUID resumeId, String userEmail) {

		CandidateProfile profile = getOwnedProfile(resumeId, userEmail);

		return candidateProfileMapper.toResponse(profile);
	}

	@Override
	@Transactional
	public CandidateProfileResponse getLatestProfile(String userEmail) {

		CandidateProfile profile = candidateProfileRepository.findFirstByUserEmailOrderByUpdatedAtDesc(userEmail)
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

		if (profile.getStatus() == ProfileStatus.CONFIRMED) {

			profile.setStatus(ProfileStatus.REVIEW_REQUIRED);

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

		profile.setStatus(ProfileStatus.CONFIRMED);

		profile.setConfirmedAt(LocalDateTime.now());

		CandidateProfile savedProfile = candidateProfileRepository.save(profile);

		return candidateProfileMapper.toResponse(savedProfile);
	}

	private CandidateProfile getOwnedProfile(UUID resumeId, String userEmail) {

		return candidateProfileRepository.findByResumeIdAndUserEmail(resumeId, userEmail)
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

		if (requests == null) {
			return;
		}

		for (CandidateSkillRequest request : requests) {

			CandidateSkill skill = CandidateSkill.builder().skillName(request.skillName().trim())
					.category(request.category()).proficiency(request.proficiency())
					.yearsOfExperience(request.yearsOfExperience())
					.source(request.source() != null ? request.source() : SkillSource.USER_ADDED)
					.evidence(normalize(request.evidence())).verified(request.verified()).build();

			profile.addSkill(skill);
		}
	}

	private void replaceProjects(CandidateProfile profile, List<CandidateProjectRequest> requests) {

		profile.getProjects().clear();

		if (requests == null) {
			return;
		}

		for (CandidateProjectRequest request : requests) {

			CandidateProject project = CandidateProject.builder().projectName(request.projectName().trim())
					.domain(normalize(request.domain())).projectDescription(normalize(request.projectDescription()))
					.candidateRole(normalize(request.candidateRole()))
					.responsibilities(normalize(request.responsibilities()))
					.businessImpact(normalize(request.businessImpact())).startDate(request.startDate())
					.endDate(request.endDate()).currentProject(request.currentProject()).build();

			addTechnologies(project, request.technologies());

			profile.addProject(project);
		}
	}

	private void addTechnologies(CandidateProject project, List<ProjectTechnologyRequest> requests) {

		if (requests == null) {
			return;
		}

		for (ProjectTechnologyRequest request : requests) {

			ProjectTechnology technology = ProjectTechnology.builder().technologyName(request.technologyName().trim())
					.usageDescription(normalize(request.usageDescription())).verified(request.verified()).build();

			project.addTechnology(technology);
		}
	}

	private void validateBeforeConfirmation(CandidateProfile profile) {

		if (profile.getFullName() == null || profile.getFullName().isBlank()) {

			throw new BadRequestException("Full name is required before profile confirmation");
		}

		if (profile.getProfessionalTitle() == null || profile.getProfessionalTitle().isBlank()) {

			throw new BadRequestException("Professional title is required before profile confirmation");
		}

		if (profile.getTargetRole() == null || profile.getTargetRole().isBlank()) {

			throw new BadRequestException("Target role is required before profile confirmation");
		}

		if (profile.getSkills().isEmpty()) {
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
}
