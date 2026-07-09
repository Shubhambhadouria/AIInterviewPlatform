package com.aiinterviewcoach.modules.interview.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aiinterviewcoach.common.enums.InterviewStatus;
import com.aiinterviewcoach.modules.user.entity.User;

@Entity
@Table(name = "interview_sessions")
@Data
public class InterviewSession {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String title;

	private String topic;
	// Java, React, SQL, System Design, Job Role, Resume Based

	private String targetRole;
	// Java Backend Developer, Full Stack Developer, DevOps Engineer

	private String experienceLevel;
	// Fresher, 1 Year, 3 Years, 5+ Years

	@Enumerated(EnumType.STRING)
	private InterviewStatus status;

	private Integer totalQuestions;

	private Integer answeredQuestions;

	private Integer overallScore;

	@Column(length = 5000)
	private String aiSummaryFeedback;

	private LocalDateTime startedAt;

	private LocalDateTime completedAt;

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InterviewQuestion> questions = new ArrayList<>();

	public InterviewSession() {
	}

	// getters and setters
}