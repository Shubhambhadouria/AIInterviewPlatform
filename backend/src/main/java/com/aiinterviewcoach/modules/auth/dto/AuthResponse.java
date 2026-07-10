package com.aiinterviewcoach.modules.auth.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record AuthResponse(
		UUID userId,
	    String fullName,
	    String email,
	    String role,
	    String token,
	    String message) {
}
