package com.aiinterviewcoach.modules.questionbank.controller;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinterviewcoach.modules.questionbank.dto.CandidateProfileResponse;
import com.aiinterviewcoach.modules.questionbank.dto.UpdateCandidateProfileRequest;
import com.aiinterviewcoach.modules.questionbank.service.CandidateProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
@Tag(name = "Candidate Profile", description = "Review, update and confirm resume-based candidate profiles")
@SecurityRequirement(name = "bearerAuth")
public class CandidateProfileController {

	private final CandidateProfileService candidateProfileService;

	@Operation(summary = "Get candidate profile by resume")
	@GetMapping("/resume/{resumeId}")
	public CandidateProfileResponse getProfileByResume(

			@PathVariable("resumeId") UUID resumeId,

			@Parameter(hidden = true) Authentication authentication) {

		return candidateProfileService.getProfileByResume(resumeId, authentication.getName());
	}

	@Operation(summary = "Get latest candidate profile")
	@GetMapping("/latest")
	public CandidateProfileResponse getLatestProfile(

			@Parameter(hidden = true) Authentication authentication) {

		return candidateProfileService.getLatestProfile(authentication.getName());
	}

	@Operation(summary = "Update candidate profile", description = "Updates profile details and replaces the skills and projects collections")
	@PutMapping("/resume/{resumeId}")
	public CandidateProfileResponse updateProfile(

			@PathVariable("resumeId") UUID resumeId,

			@Valid @RequestBody UpdateCandidateProfileRequest request,

			@Parameter(hidden = true) Authentication authentication) {

		return candidateProfileService.updateProfile(resumeId, request, authentication.getName());
	}

	@Operation(summary = "Confirm candidate profile", description = "Confirms that the profile information is accurate and ready for question generation")
	@PostMapping("/resume/{resumeId}/confirm")
	public CandidateProfileResponse confirmProfile(

			@PathVariable("resumeId") UUID resumeId,

			@Parameter(hidden = true) Authentication authentication) {

		return candidateProfileService.confirmProfile(resumeId, authentication.getName());
	}
}
