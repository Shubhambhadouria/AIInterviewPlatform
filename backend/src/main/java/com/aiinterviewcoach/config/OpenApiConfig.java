package com.aiinterviewcoach.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	public static final String SECURITY_SCHEME_NAME = "bearerAuth";

	@Bean
	public OpenAPI aiInterviewCoachOpenAPI() {

		SecurityScheme securityScheme = new SecurityScheme().name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP)
				.scheme("bearer").bearerFormat("JWT").description("Enter the JWT token generated from the login API");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

		return new OpenAPI()
				.info(new Info().title("AI Interview Coach API").version("1.0.0").description("""
						REST API documentation for the AI Interview Coach platform.

						Main features:
						- JWT authentication
						- Start an interview session
						- Submit interview answers
						- Gemini-based AI evaluation
						- View final interview results
						""").contact(new Contact().name("Shubham Singh Bhadouria"))
						.license(new License().name("Private Project")))
				.addSecurityItem(securityRequirement)
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
	}
}
