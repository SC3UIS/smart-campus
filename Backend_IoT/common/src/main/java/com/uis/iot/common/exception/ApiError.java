package com.uis.iot.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {
	
	private HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;

	private String message;
	
	private String exception;

	private ApiError() {
		timestamp = LocalDateTime.now();
	}

	public ApiError(final HttpStatus status) {
		this();
		this.status = status;
	}

	public ApiError(final HttpStatus status, final String message) {
		this();
		this.status = status;
		this.message = message;
	}

	public ApiError(final HttpStatus status, final Exception e) {
		this();
		this.status = status;
		this.message = e.getMessage();
		this.exception = e.getClass().getSimpleName();
	}

	public static ResponseEntity<Object> buildResponseEntity(final ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
	public String getException() {
		return exception;
	}
}
