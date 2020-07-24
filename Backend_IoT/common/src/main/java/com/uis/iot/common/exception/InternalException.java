package com.uis.iot.common.exception;

public class InternalException extends DomainException {

	private static final long serialVersionUID = 1L;

	public InternalException(String message) {
		super(message);
	}
	
	public InternalException(String message, Exception e) {
		super(message, e);
	}

}
