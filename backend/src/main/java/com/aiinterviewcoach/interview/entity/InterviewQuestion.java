package com.aiinterviewcoach.interview.entity;

import java.util.UUID;

import com.aiinterviewcoach.interview.InterviewSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class InterviewQuestion {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String questionText;

    @Column(length = 3000)
    private String userAnswer;

    @Column(length = 3000)
    private String aiFeedback;

    private Integer score;

    @ManyToOne
    private InterviewSession interviewSession;
}
