package com.aiinterviewcoach.modules.interview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiinterviewcoach.modules.interview.entity.InterviewSession;
import com.aiinterviewcoach.modules.user.entity.User;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

	List<InterviewSession> findByUserOrderByCreatedAtDesc(User user);
}
