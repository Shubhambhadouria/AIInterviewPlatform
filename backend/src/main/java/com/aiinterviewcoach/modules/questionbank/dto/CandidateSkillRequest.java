package com.aiinterviewcoach.modules.questionbank.dto;

import java.math.BigDecimal;

import com.aiinterviewcoach.modules.questionbank.enums.SkillCategory;
import com.aiinterviewcoach.modules.questionbank.enums.SkillProficiency;
import com.aiinterviewcoach.modules.questionbank.enums.SkillSource;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CandidateSkillRequest(

		@NotBlank(message = "Skill name is required") String skillName,

		@NotNull(message = "Skill category is required") SkillCategory category,

		SkillProficiency proficiency,

		@DecimalMin(value = "0.0", message = "Years of experience cannot be negative") @DecimalMax(value = "60.0", message = "Years of experience cannot exceed 60") BigDecimal yearsOfExperience,

		SkillSource source,

		String evidence,

		boolean verified

) {
}