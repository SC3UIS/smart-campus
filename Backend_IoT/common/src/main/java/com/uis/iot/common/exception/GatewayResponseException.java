package com.uis.iot.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayResponseException {

	private HttpStatus status;

	private String timestamp;

	private String message;

	private String exception;
	
	private String error;

	private GatewayResponseException() {
		timestamp = LocalDateTime.now().toString();
	}

	public GatewayResponseException(final HttpStatus status) {
		this();
		this.status = status;
	}

	public GatewayResponseException(final HttpStatus status, final String message) {
		this();
		this.status = status;
		this.message = message;
	}

	public GatewayResponseException(final HttpStatus status, final Exception e) {
		this();
		this.status = status;
		this.message = e.getMessage();
		this.exception = e.getClass().getSimpleName();
	}

	public static ResponseEntity<Object> buildResponseEntity(final GatewayResponseException apiError) {
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	public String getError() {
		return error;
	}

}
