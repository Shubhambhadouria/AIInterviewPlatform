package com.aiinterviewcoach.modules.interview.service;

import java.util.UUID;

import com.aiinterviewcoach.modules.interview.dto.InterviewResultResponse;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewRequest;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewResponse;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerRequest;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerResponse;

public interface InterviewService {
	StartInterviewResponse startInterview(StartInterviewRequest request, String userEmail);

	SubmitAnswerResponse submitAnswer(UUID sessionId, SubmitAnswerRequest request, String userEmail);

	InterviewResultResponse getResult(UUID sessionId, String userEmail);
}
