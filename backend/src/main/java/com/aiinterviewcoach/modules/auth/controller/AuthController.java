package com.aiinterviewcoach.modules.auth.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinterviewcoach.modules.auth.dto.AuthResponse;
import com.aiinterviewcoach.modules.auth.dto.LoginRequest;
import com.aiinterviewcoach.modules.auth.dto.RegisterRequest;
import com.aiinterviewcoach.modules.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public AuthResponse register(@RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping("/me")
	public AuthResponse me(Authentication authentication) {
	    String email = authentication.getName();
	    return authService.getCurrentUser(email);
	}
}
