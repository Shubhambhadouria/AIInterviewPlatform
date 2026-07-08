package com.aiinterviewcoach.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aiinterviewcoach.auth.dto.AuthResponse;
import com.aiinterviewcoach.auth.dto.LoginRequest;
import com.aiinterviewcoach.auth.dto.RegisterRequest;
import com.aiinterviewcoach.security.JwtService;
import com.aiinterviewcoach.user.Role;
import com.aiinterviewcoach.user.entity.User;
import com.aiinterviewcoach.user.repository.UserRepository;

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

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		User user = User.builder().fullName(request.getFullName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.USER).createdAt(LocalDateTime.now())
				.build();

		User savedUser = userRepository.save(user);

		String token = jwtService.generateToken(savedUser);

		return AuthResponse.builder().userId(savedUser.getId()).fullName(savedUser.getFullName())
				.email(savedUser.getEmail()).role(savedUser.getRole().name()).token(token)
				.message("User registered successfully").build();
	}

	public AuthResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!passwordMatches) {
			throw new RuntimeException("Invalid email or password");
		}

		String token = jwtService.generateToken(user);

		return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.role(user.getRole().name()).token(token).message("Login successful").build();
	}

	public AuthResponse getCurrentUser(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.role(user.getRole().name()).message("Current user fetched successfully").build();
	}
}
