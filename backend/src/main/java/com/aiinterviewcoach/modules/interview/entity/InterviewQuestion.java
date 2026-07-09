package com.aiinterviewcoach.modules.interview.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_questions")
@Data
public class InterviewQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(length = 3000, nullable = false)
	private String questionText;

	private String questionType;
	// TECHNICAL, HR, SYSTEM_DESIGN, CODING, RESUME_BASED, JOB_MATCHING

	private String difficulty;
	// EASY, MEDIUM, HARD

	@Column(length = 5000)
	private String expectedAnswer;

	@Column(length = 5000)
	private String userAnswer;

	@Column(length = 5000)
	private String aiFeedback;
	
	@Column(length = 5000)
	private String improvedAnswer;

	private Integer score;

	private Boolean answered;

	private LocalDateTime answeredAt;

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_session_id")
	private InterviewSession interviewSession;

	public InterviewQuestion() {
	}

	// getters and setters
}