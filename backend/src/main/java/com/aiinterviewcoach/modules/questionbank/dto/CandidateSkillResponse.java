package com.aiinterviewcoach.modules.questionbank.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.enums.SkillCategory;
import com.aiinterviewcoach.modules.questionbank.enums.SkillProficiency;
import com.aiinterviewcoach.modules.questionbank.enums.SkillSource;

public record CandidateSkillResponse(

		UUID id,

		String skillName,

		SkillCategory category,

		SkillProficiency proficiency,

		BigDecimal yearsOfExperience,

		SkillSource source,

		String evidence,

		boolean verified

) {
}