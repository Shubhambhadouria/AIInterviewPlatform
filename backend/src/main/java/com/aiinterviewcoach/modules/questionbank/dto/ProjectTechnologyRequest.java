package com.aiinterviewcoach.modules.questionbank.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectTechnologyRequest(

		@NotBlank(message = "Technology name is required") String technologyName,
		String usageDescription,

		boolean verified

) {
}
