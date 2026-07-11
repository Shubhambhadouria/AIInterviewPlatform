package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.UUID;

public record ProjectTechnologyResponse(

		UUID id, String technologyName, String usageDescription, boolean verified

) {
}
