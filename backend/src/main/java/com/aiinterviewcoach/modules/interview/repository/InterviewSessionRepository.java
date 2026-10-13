package com.aiinterviewcoach.modules.interview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiinterviewcoach.modules.interview.entity.InterviewSession;
import com.aiinterviewcoach.modules.user.entity.User;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
	@org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
	@org.springframework.data.jpa.repository.Query("select s from InterviewSession s where s.id = :id")
	java.util.Optional<InterviewSession> findLockedById(@org.springframework.data.repository.query.Param("id") UUID id);

	List<InterviewSession> findByUserOrderByCreatedAtDesc(User user);
}
