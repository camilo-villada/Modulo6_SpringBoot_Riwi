package com.java.eventify.exception;

import com.java.eventify.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DomainValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleDomainValidation(
			DomainValidationException ex,
			HttpServletRequest request
	) {
		return ResponseEntity.badRequest().body(buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				ex.getMessage(),
				request.getRequestURI()
		));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request
	) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(
				HttpStatus.NOT_FOUND,
				ex.getMessage(),
				request.getRequestURI()
		));
	}

	private ApiErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
		return ApiErrorResponse.builder()
				.timestamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message)
				.path(path)
				.build();
	}
}
