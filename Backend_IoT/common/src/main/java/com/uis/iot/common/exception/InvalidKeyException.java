package com.uis.iot.common.exception;

public class InvalidKeyException extends DomainException {

	private static final long serialVersionUID = 1L;

	public InvalidKeyException(String message) {
		super(message);
	}
	
	public InvalidKeyException(String message, Exception e) {
		super(message, e);
	}

}
