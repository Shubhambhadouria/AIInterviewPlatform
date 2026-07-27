package com.aiinterviewcoach.modules.questionbank.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParsedProject(

		String name,

		String description,

		String role,

		String responsibilities,

		String businessImpact,

		List<String> technologies) {

	public ParsedProject {
		technologies = technologies == null ? new ArrayList<>() : technologies;
	}
}
