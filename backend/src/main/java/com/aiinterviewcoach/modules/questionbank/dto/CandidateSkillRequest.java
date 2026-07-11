package com.aiinterviewcoach.modules.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import com.aiinterviewcoach.modules.questionbank.entity.SkillCategory;
import com.aiinterviewcoach.modules.questionbank.entity.SkillProficiency;
import com.aiinterviewcoach.modules.questionbank.entity.SkillSource;

public record CandidateSkillRequest(

		@NotBlank(message = "Skill name is required") String skillName,
		@NotNull(message = "Skill category is required") SkillCategory category,
		SkillProficiency proficiency,

		BigDecimal yearsOfExperience,

		SkillSource source,

		String evidence,

		boolean verified

) {
}