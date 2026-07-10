package com.aiinterviewcoach.modules.auth.dto;

public record LoginRequest(
		String email,
		String password) {
}
