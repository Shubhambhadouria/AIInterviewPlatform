package com.aiinterviewcoach.modules.questionbank.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.aiinterviewcoach.modules.questionbank.entity.SkillCategory;
import com.aiinterviewcoach.modules.questionbank.entity.SkillProficiency;
import com.aiinterviewcoach.modules.questionbank.entity.SkillSource;

public record CandidateSkillResponse(

		UUID id, String skillName, SkillCategory category, SkillProficiency proficiency, BigDecimal yearsOfExperience,
		SkillSource source, String evidence, boolean verified

) {
}