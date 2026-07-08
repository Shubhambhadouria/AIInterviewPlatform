package com.aiinterviewcoach.interview.dto;

import lombok.Data;

@Data
public class StartInterviewRequest {
	private String topic; // Java, React, SQL, System Design
    private Integer totalQuestions;
}
