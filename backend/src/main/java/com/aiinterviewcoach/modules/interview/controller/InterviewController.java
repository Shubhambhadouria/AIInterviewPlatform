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
	private final com.aiinterviewcoach.modules.interview.service.impl.InterviewServiceImpl sessions;

	private final InterviewService interviewService;

	public InterviewController(InterviewService interviewService, com.aiinterviewcoach.modules.interview.service.impl.InterviewServiceImpl sessions) {
		this.interviewService = interviewService;
		this.sessions = sessions;
	}
	@GetMapping("/history")
	public java.util.List<com.aiinterviewcoach.modules.interview.service.impl.InterviewServiceImpl.HistoryView> history(Authentication authentication) {
		return sessions.history(authentication.getName());
	}
	@GetMapping("/{sessionId}")
	public com.aiinterviewcoach.modules.interview.service.impl.InterviewServiceImpl.SessionView session(@PathVariable UUID sessionId, Authentication authentication) {
		return sessions.getSession(sessionId, authentication.getName());
	}

	@PostMapping("/start")
	public StartInterviewResponse startInterview(@jakarta.validation.Valid @RequestBody StartInterviewRequest request,
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
