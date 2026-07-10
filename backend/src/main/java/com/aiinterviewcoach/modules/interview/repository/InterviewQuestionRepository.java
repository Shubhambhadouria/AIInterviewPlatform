package com.aiinterviewcoach.modules.interview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiinterviewcoach.modules.interview.entity.InterviewQuestion;
import com.aiinterviewcoach.modules.interview.entity.InterviewSession;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, UUID> {
	List<InterviewQuestion> findByInterviewSession(InterviewSession interviewSession);
}
