package com.aiinterviewcoach.modules.auth.dto;

public record RegisterRequest(
		String fullName,
		String email,
		String password) {
}
