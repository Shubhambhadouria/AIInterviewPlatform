package com.aiinterviewcoach.modules.questionbank.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.aiinterviewcoach.modules.questionbank.dto.CandidateProfileResponse;
import com.aiinterviewcoach.modules.questionbank.dto.CandidateProjectResponse;
import com.aiinterviewcoach.modules.questionbank.dto.CandidateSkillResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ProjectTechnologyResponse;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProject;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateSkill;
import com.aiinterviewcoach.modules.questionbank.entity.ProjectTechnology;

@Component
public class CandidateProfileMapper {

	public CandidateProfileResponse toResponse(CandidateProfile profile) {

		List<CandidateSkillResponse> skills = profile.getSkills().stream().map(this::toSkillResponse).toList();

		List<CandidateProjectResponse> projects = profile.getProjects().stream().map(this::toProjectResponse).toList();

		return new CandidateProfileResponse(profile.getId(), profile.getResume().getId(), profile.getFullName(),
				profile.getProfessionalTitle(), profile.getTotalExperienceMonths(), profile.getProfessionalSummary(),
				profile.getCurrentCompany(), profile.getCurrentRole(), profile.getTargetRole(), profile.getStatus(),
				profile.getConfirmedAt(), profile.getCreatedAt(), profile.getUpdatedAt(), skills, projects);
	}

	private CandidateSkillResponse toSkillResponse(CandidateSkill skill) {

		return new CandidateSkillResponse(skill.getId(), skill.getSkillName(), skill.getCategory(),
				skill.getProficiency(), skill.getYearsOfExperience(), skill.getSource(), skill.getEvidence(),
				skill.isVerified());
	}

	private CandidateProjectResponse toProjectResponse(CandidateProject project) {

		List<ProjectTechnologyResponse> technologies = project.getTechnologies().stream()
				.map(this::toTechnologyResponse).toList();

		return new CandidateProjectResponse(project.getId(), project.getProjectName(), project.getDomain(),
				project.getProjectDescription(), project.getCandidateRole(), project.getResponsibilities(),
				project.getBusinessImpact(), project.getStartDate(), project.getEndDate(), project.isCurrentProject(),
				technologies);
	}

	private ProjectTechnologyResponse toTechnologyResponse(ProjectTechnology technology) {

		return new ProjectTechnologyResponse(technology.getId(), technology.getTechnologyName(),
				technology.getUsageDescription(), technology.isVerified());
	}
}
