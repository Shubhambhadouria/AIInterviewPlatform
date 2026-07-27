package com.aiinterviewcoach.modules.questionbank.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiinterviewcoach.modules.questionbank.dto.ResumeParseResponse;
import com.aiinterviewcoach.modules.questionbank.dto.ResumeUploadResponse;
import com.aiinterviewcoach.modules.questionbank.service.ResumeParsingService;
import com.aiinterviewcoach.modules.questionbank.service.ResumeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

	private final ResumeService resumeService;
	private final ResumeParsingService resumeParsingService;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResumeUploadResponse> upload(@RequestPart("file") MultipartFile file,
			Authentication authentication) {

		ResumeUploadResponse response = resumeService.upload(file, authentication.getName());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{resumeId}/parse")
	public ResponseEntity<ResumeParseResponse> parse(@PathVariable("resumeId") UUID resumeId, Authentication authentication) {

		ResumeParseResponse response = resumeParsingService.parse(resumeId, authentication.getName());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{resumeId}")
	public ResponseEntity<Void> delete(@PathVariable UUID resumeId, Authentication authentication) {

		resumeService.delete(resumeId, authentication.getName());

		return ResponseEntity.noContent().build();
	}
}