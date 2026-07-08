package com.aiinterviewcoach.interview;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aiinterviewcoach.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String topic;

	private Integer totalQuestions;

	private Integer score;

	@Enumerated(EnumType.STRING)
	private InterviewStatus status;

	private LocalDateTime startedAt;

	private LocalDateTime completedAt;

	@ManyToOne
	private User user;
}
