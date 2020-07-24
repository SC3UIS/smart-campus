package com.uis.iot.common.exception;

public class RecordExistsException extends DomainException {

	private static final long serialVersionUID = 1L;

	public RecordExistsException(String message) {
		super(message);
	}
	
	public RecordExistsException(String message, Exception e) {
		super(message, e);
	}

}
