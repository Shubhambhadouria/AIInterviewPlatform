package com.aiinterviewcoach.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aiinterviewcoach.modules.user.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(User user) {
		return Jwts.builder().subject(user.getEmail()).claim("role", user.getRole().name()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSigningKey()).compact();
	}

	public String extractEmail(String token) {
		Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

		return claims.getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
	    String email = extractEmail(token);
	    return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
	    Claims claims = Jwts.parser()
	            .verifyWith(getSigningKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();

	    return claims.getExpiration();
	}
}