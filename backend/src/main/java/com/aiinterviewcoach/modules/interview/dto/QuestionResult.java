package com.aiinterviewcoach.modules.interview.dto;
import java.util.UUID;
public record QuestionResult(UUID questionId, String questionText, String userAnswer, Integer score,
    String aiFeedback, String improvedAnswer) {}
