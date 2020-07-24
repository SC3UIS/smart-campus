package co.uis.iot.edge.core.utils;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Response used when an error occurred.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class ApiError {
	
	private HttpStatus status;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;
	
	private String message;

	private ApiError() {
		timestamp = LocalDateTime.now();
	}

	ApiError(HttpStatus status) {
		this();
		this.status = status;
	}

	public ApiError(HttpStatus status, String message) {
		this();
		this.status = status;
		this.message = message;
	}
	
	public ApiError(HttpStatus status, Exception e) {
		this();
		this.status = status;
		this.message = e.getMessage();
	}

	/**
	 * Creates a {@link ResponseEntity} for a given error.
	 * 
	 * @param apiError to create the Response Entity. Not null.
	 * @return the Response Entity.
	 */
	public static ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
