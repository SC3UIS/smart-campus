package com.uis.iot.common.exception;

public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DomainException(String message) {
		super(message);
	}
	
	public DomainException(String message, Exception e) {
		super(message, e);
	}
}
