package com.java.eventify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DomainValidationException.class)
	public ResponseEntity<ProblemDetail> handleDomainValidation(DomainValidationException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		pd.setTitle("Domain Validation Error");
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		pd.setTitle("Resource Not Found");
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ProblemDetail> handleDuplicateResource(DuplicateResourceException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		pd.setTitle("Duplicate Resource");
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
	}

	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
		pd.setTitle("Business Rule Violation");
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(pd);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields");
		pd.setTitle("Validation Error");
		pd.setProperty("timestamp", Instant.now());
		pd.setProperty("message", "Validation failed for one or more fields");

		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		pd.setProperty("errors", errors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
	}
}
