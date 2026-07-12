package com.aiinterviewcoach.modules.questionbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinterviewcoach.modules.questionbank.dto.GenerateQuestionBankRequest;
import com.aiinterviewcoach.modules.questionbank.dto.QuestionBankSummaryResponse;
import com.aiinterviewcoach.modules.questionbank.service.QuestionBankService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/question-banks")
@RequiredArgsConstructor
public class QuestionBankController {
	private final QuestionBankService questionBankService;

	@PostMapping
	public ResponseEntity<QuestionBankSummaryResponse> generate(@Valid @RequestBody GenerateQuestionBankRequest request,
			Authentication authentication) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(questionBankService.generate(request, authentication.getName()));
	}
}
