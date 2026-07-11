package com.aiinterviewcoach.modules.questionbank.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Upload and manage candidate resumes")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

	private final ResumeService resumeService;

	@Operation(summary = "Upload resume", description = "Uploads a PDF or DOCX resume and creates a draft candidate profile")
	@ApiResponse(responseCode = "201", description = "Resume uploaded successfully")
	@ApiResponse(responseCode = "400", description = "Invalid resume file", content = @Content)
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResumeUploadResponse uploadResume(

			@RequestPart("file") MultipartFile file,

			@Parameter(hidden = true) Authentication authentication) {

		return resumeService.uploadResume(file, authentication.getName());
	}

	@Operation(summary = "Get all uploaded resumes")
	@GetMapping
	public List<ResumeResponse> getResumes(@Parameter(hidden = true) Authentication authentication) {

		return resumeService.getUserResumes(authentication.getName());
	}

	@Operation(summary = "Get resume metadata")
	@GetMapping("/{resumeId}")
	public ResumeResponse getResume(

			@PathVariable UUID resumeId,

			@Parameter(hidden = true) Authentication authentication) {

		return resumeService.getResume(resumeId, authentication.getName());
	}

	@Operation(summary = "Set resume as active")
	@PatchMapping("/{resumeId}/activate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void activateResume(

			@PathVariable UUID resumeId,

			@Parameter(hidden = true) Authentication authentication) {

		resumeService.activateResume(resumeId, authentication.getName());
	}

	@Operation(summary = "Delete resume")
	@DeleteMapping("/{resumeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteResume(

			@PathVariable UUID resumeId,

			@Parameter(hidden = true) Authentication authentication) {

		resumeService.deleteResume(resumeId, authentication.getName());
	}
}
