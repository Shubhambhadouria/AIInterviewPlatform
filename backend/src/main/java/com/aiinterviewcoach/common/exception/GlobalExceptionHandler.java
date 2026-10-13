package com.aiinterviewcoach.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);
	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleConflict(Exception ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, "This request conflicts with an existing record.", request.getRequestURI());
	}
	@ExceptionHandler({org.springframework.web.bind.MethodArgumentNotValidException.class,
		org.springframework.http.converter.HttpMessageNotReadableException.class,
		org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> handleValidation(Exception ex, HttpServletRequest request) {
		String message = "Invalid request. Check the fields and try again.";
		if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException invalid) {
			message = invalid.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(java.util.stream.Collectors.joining("; "));
		}
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}
	@ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleUploadSize(Exception ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Resume must be 10 MB or smaller", request.getRequestURI());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unhandled request failure for {}", request.getRequestURI(), ex);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.",
				request.getRequestURI());
	}

	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException exception,
			HttpServletRequest request) {

		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				"File Storage Error", exception.getMessage(), request.getRequestURI());

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(AiProcessingException.class)
	public ResponseEntity<ErrorResponse> handleAiProcessingException(AiProcessingException exception,
			HttpServletRequest request) {

		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(),
				"AI Processing Error", exception.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
				message, path);

		return new ResponseEntity<>(errorResponse, status);
	}
}
