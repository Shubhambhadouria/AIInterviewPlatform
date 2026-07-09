package com.aiinterviewcoach.modules.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aiinterviewcoach.common.enums.Role;
import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.common.exception.ResourceNotFoundException;
import com.aiinterviewcoach.modules.auth.dto.AuthResponse;
import com.aiinterviewcoach.modules.auth.dto.LoginRequest;
import com.aiinterviewcoach.modules.auth.dto.RegisterRequest;
import com.aiinterviewcoach.modules.user.entity.User;
import com.aiinterviewcoach.modules.user.repository.UserRepository;
import com.aiinterviewcoach.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("Email already registered");
		}

		User user = User.builder().fullName(request.fullName()).email(request.email())
				.password(passwordEncoder.encode(request.password())).role(Role.USER).createdAt(LocalDateTime.now())
				.build();

		User savedUser = userRepository.save(user);

		String token = jwtService.generateToken(savedUser);

		return AuthResponse.builder().userId(savedUser.getId()).fullName(savedUser.getFullName())
				.email(savedUser.getEmail()).role(savedUser.getRole().name()).token(token)
				.message("User registered successfully").build();
	}

	public AuthResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BadRequestException("Invalid email or password"));

		boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

		if (!passwordMatches) {
			throw new BadRequestException("Invalid email or password");
		}

		String token = jwtService.generateToken(user);

		return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.role(user.getRole().name()).token(token).message("Login successful").build();
	}

	public AuthResponse getCurrentUser(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.role(user.getRole().name()).message("Current user fetched successfully").build();
	}
}
