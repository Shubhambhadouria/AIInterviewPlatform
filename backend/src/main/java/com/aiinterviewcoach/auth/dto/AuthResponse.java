package com.aiinterviewcoach.auth.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private UUID userId;
	private String fullName;
	private String email;
	private String role;
	private String message;
}
