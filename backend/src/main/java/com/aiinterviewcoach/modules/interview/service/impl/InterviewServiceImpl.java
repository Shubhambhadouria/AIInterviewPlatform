package com.aiinterviewcoach.modules.interview.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aiinterviewcoach.common.enums.InterviewStatus;
import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.common.exception.UnauthorizedException;
import com.aiinterviewcoach.modules.ai.dto.AiEvaluationResponse;
import com.aiinterviewcoach.modules.ai.service.AiEvaluationService;
import com.aiinterviewcoach.modules.interview.dto.InterviewQuestionResponse;
import com.aiinterviewcoach.modules.interview.dto.InterviewResultResponse;
import com.aiinterviewcoach.modules.interview.dto.QuestionResult;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewRequest;
import com.aiinterviewcoach.modules.interview.dto.StartInterviewResponse;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerRequest;
import com.aiinterviewcoach.modules.interview.dto.SubmitAnswerResponse;
import com.aiinterviewcoach.modules.interview.entity.InterviewQuestion;
import com.aiinterviewcoach.modules.interview.entity.InterviewSession;
import com.aiinterviewcoach.modules.interview.repository.InterviewQuestionRepository;
import com.aiinterviewcoach.modules.interview.repository.InterviewSessionRepository;
import com.aiinterviewcoach.modules.interview.service.InterviewService;
import com.aiinterviewcoach.modules.user.entity.User;
import com.aiinterviewcoach.modules.user.repository.UserRepository;

@Service
public class InterviewServiceImpl implements InterviewService {
	private final InterviewSessionRepository interviewSessionRepository;
	private final InterviewQuestionRepository interviewQuestionRepository;
	private final UserRepository userRepository;
	private final AiEvaluationService aiEvaluationService;

	public InterviewServiceImpl(InterviewSessionRepository interviewSessionRepository,
			InterviewQuestionRepository interviewQuestionRepository, UserRepository userRepository,
			AiEvaluationService aiEvaluationService) {
		this.interviewSessionRepository = interviewSessionRepository;
		this.interviewQuestionRepository = interviewQuestionRepository;
		this.userRepository = userRepository;
		this.aiEvaluationService = aiEvaluationService;
	}

	@Override
	public StartInterviewResponse startInterview(StartInterviewRequest request, String userEmail) {

		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		int totalQuestions = request.totalQuestions() == null ? 5 : request.totalQuestions();

		InterviewSession session = new InterviewSession();
		session.setTitle(request.targetRole() + " Interview Practice");
		session.setTopic(request.topic());
		session.setTargetRole(request.targetRole());
		session.setExperienceLevel(request.experienceLevel());
		session.setStatus(InterviewStatus.IN_PROGRESS);
		session.setTotalQuestions(totalQuestions);
		session.setAnsweredQuestions(0);
		session.setOverallScore(0);
		session.setCreatedAt(LocalDateTime.now());
		session.setStartedAt(LocalDateTime.now());
		session.setUser(user);

		InterviewSession savedSession = interviewSessionRepository.save(session);

		List<String> questions = generateHardcodedQuestions(request.topic(), request.targetRole());

		List<InterviewQuestionResponse> questionResponses = new ArrayList<>();

		for (int i = 0; i < Math.min(totalQuestions, questions.size()); i++) {

			InterviewQuestion question = new InterviewQuestion();
			question.setQuestionText(questions.get(i));
			question.setQuestionType("TECHNICAL");
			question.setDifficulty("MEDIUM");
			question.setAnswered(false);
			question.setCreatedAt(LocalDateTime.now());
			question.setInterviewSession(savedSession);

			InterviewQuestion savedQuestion = interviewQuestionRepository.save(question);

			questionResponses.add(new InterviewQuestionResponse(savedQuestion.getId(), savedQuestion.getQuestionText(),
					savedQuestion.getQuestionType(), savedQuestion.getDifficulty()));
		}

		return new StartInterviewResponse(savedSession.getId(), savedSession.getTitle(), savedSession.getTopic(),
				savedSession.getTargetRole(), savedSession.getStatus().name(), questionResponses);
	}

	private List<String> generateHardcodedQuestions(String topic, String targetRole) {

		List<String> questions = new ArrayList<>();

		questions.add("Explain your experience with " + topic + " in your previous project.");
		questions.add("How would you design a scalable backend system for a " + targetRole + " role?");
		questions.add("What challenges did you face while working with " + topic + "?");
		questions.add("How do you handle errors, logging, and monitoring in backend applications?");
		questions.add("Explain one performance optimization you implemented in your project.");
		questions.add("How would you use Redis in a real-world application?");
		questions.add("How would you containerize a Spring Boot and React application using Docker?");
		questions.add("What is your understanding of Kubernetes deployment and services?");

		return questions;
	}

	@Override
	public SubmitAnswerResponse submitAnswer(UUID sessionId, SubmitAnswerRequest request, String userEmail) {

		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		InterviewSession session = interviewSessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

		if (!session.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedException("You are not allowed to access this session");
		}

		InterviewQuestion question = interviewQuestionRepository.findById(request.questionId())
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));

		if (!question.getInterviewSession().getId().equals(session.getId())) {
			throw new BadRequestException("Question does not belong to this interview session");
		}

		if (Boolean.TRUE.equals(question.getAnswered())) {
			throw new BadRequestException("This question is already answered");
		}

		question.setUserAnswer(request.answer());
		question.setAnswered(true);
		question.setAnsweredAt(LocalDateTime.now());

		AiEvaluationResponse aiResponse = aiEvaluationService.evaluateAnswer(question.getQuestionText(),
				request.answer(), session.getTopic(), session.getTargetRole(), session.getExperienceLevel());

		int score = aiResponse.score();
		String feedback = aiResponse.feedback();

		question.setScore(score);
		question.setAiFeedback(feedback);
		question.setImprovedAnswer(aiResponse.improvedAnswer());

		interviewQuestionRepository.save(question);

		session.setAnsweredQuestions(session.getAnsweredQuestions() + 1);

		if (session.getAnsweredQuestions().equals(session.getTotalQuestions())) {
			session.setStatus(InterviewStatus.COMPLETED);
			session.setCompletedAt(LocalDateTime.now());
		}

		interviewSessionRepository.save(session);

		return new SubmitAnswerResponse(question.getId(), score, feedback, "Answer submitted successfully");
	}

	@Override
	public InterviewResultResponse getResult(UUID sessionId, String userEmail) {

		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		InterviewSession session = interviewSessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

		if (!session.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedException("You are not allowed to access this interview");
		}

		List<InterviewQuestion> questions = interviewQuestionRepository.findByInterviewSession(session);

		int overallScore = 0;

		List<QuestionResult> results = new ArrayList<>();

		for (InterviewQuestion question : questions) {

			overallScore += question.getScore() == null ? 0 : question.getScore();

			results.add(new QuestionResult(question.getId(), question.getQuestionText(), question.getUserAnswer(),
					question.getScore(), question.getAiFeedback()));
		}

		if (!questions.isEmpty()) {
			overallScore = overallScore / questions.size();
		}

		session.setOverallScore(overallScore);

		interviewSessionRepository.save(session);

		return new InterviewResultResponse(session.getId(), session.getTitle(), session.getTopic(),
				session.getTargetRole(), session.getExperienceLevel(), session.getStatus().name(),
				session.getTotalQuestions(), session.getAnsweredQuestions(), overallScore,
				session.getAiSummaryFeedback(), results);
	}
}
