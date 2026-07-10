package com.aiinterviewcoach.modules.interview.controller;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinterviewcoach.modules.interview.dto.InterviewResultResponse;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewRequest;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewResponse;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerRequest;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerResponse;
import com.aiinterviewcoach.modules.interview.service.InterviewService;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

	private final InterviewService interviewService;

	public InterviewController(InterviewService interviewService) {
		this.interviewService = interviewService;
	}

	@PostMapping("/start")
	public StartInterviewResponse startInterview(@RequestBody StartInterviewRequest request,
			Authentication authentication) {
		return interviewService.startInterview(request, authentication.getName());
	}

	@PostMapping("/{sessionId}/answer")
	public SubmitAnswerResponse submitAnswer(@PathVariable("sessionId") UUID sessionId, @RequestBody SubmitAnswerRequest request,
			Authentication authentication) {
		return interviewService.submitAnswer(sessionId, request, authentication.getName());
	}

	@GetMapping("/{sessionId}/result")
	public InterviewResultResponse getResult(@PathVariable("sessionId") UUID sessionId, Authentication authentication) {
		return interviewService.getResult(sessionId, authentication.getName());
	}

}