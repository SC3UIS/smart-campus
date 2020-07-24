package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when a Process doesn't have the TOPIC property correctly
 * set.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class MissingTopicException extends DomainException {

	private static final long serialVersionUID = 1L;

	public MissingTopicException(String message) {
		super(message);
	}
	
	public MissingTopicException() {
		super("The topic property wasn't found or has a wrong value.");
	}

}
