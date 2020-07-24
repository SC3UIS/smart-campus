package co.uis.iot.edge.core.utils;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import co.uis.iot.edge.core.exception.CommunicationException;
import co.uis.iot.edge.core.exception.DeviceNotFoundException;
import co.uis.iot.edge.core.exception.GatewayExistsException;
import co.uis.iot.edge.core.exception.MissingTopicException;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessAlredyExistException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request."));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return ApiError.buildResponseEntity(
				new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred processing the request."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(DeviceNotFoundException.class)
	protected ResponseEntity<Object> handleDeviceNotFound(DeviceNotFoundException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ProcessNotFoundException.class)
	protected ResponseEntity<Object> handleProcesNotFound(ProcessNotFoundException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ProcessAlredyExistException.class)
	protected ResponseEntity<Object> handleProcessAlreadyExists(ProcessAlredyExistException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(GatewayExistsException.class)
	protected ResponseEntity<Object> handleGatewayAlreadyExists(GatewayExistsException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(PersistenceException.class)
	protected ResponseEntity<Object> handlePersistenceError(PersistenceException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(MissingTopicException.class)
	protected ResponseEntity<Object> handleMissingTopic(MissingTopicException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.PRECONDITION_FAILED, ex));
	}

	@ExceptionHandler(CommunicationException.class)
	protected ResponseEntity<Object> handleCommunicationError(CommunicationException ex) {
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(DataAccessResourceFailureException.class)
	protected ResponseEntity<Object> handleDataAccessFailure(DataAccessResourceFailureException ex) {
		PersistenceException e = new PersistenceException("Database not available, please try again later.", ex);
		return ApiError.buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e));
	}

}
