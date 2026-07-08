package com.aiinterviewcoach.interview.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
	private UUID questionId;
	private String answer;
}
