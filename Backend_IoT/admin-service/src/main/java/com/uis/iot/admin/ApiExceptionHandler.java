package com.uis.iot.admin;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.uis.iot.common.exception.ApiError;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.exception.RecordExistsException;

@Order(Ordered.HIGHEST_PRECEDENCE - 1)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "JSON Malformado."));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return ApiError.buildResponseEntity(
				new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Un error ocurrió procesando la petición."));
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex));
	}
	@ExceptionHandler(InternalException.class)
	protected ResponseEntity<Object> handleInternalException(InternalException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}
	
	@ExceptionHandler(RecordExistsException.class)
	protected ResponseEntity<Object> handleUserExists(RecordExistsException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.NOT_ACCEPTABLE, ex));
	}
	
	@ExceptionHandler(InvalidKeyException.class)
	protected ResponseEntity<Object> handleInvalidKeyException(InvalidKeyException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
	}
}
