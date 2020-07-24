package co.uis.iot.edge.core.exception;

/**
 * General High level exception.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DomainException(String message) {
		super(message);
	}

	public DomainException(String message, Exception e) {
		super(message, e);
	}

	public DomainException(Throwable t) {
		super(t);
	}

}
