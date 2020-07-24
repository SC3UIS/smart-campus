package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when a problem occurred communicating with a MQTT broker.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class CommunicationException extends DomainException {

	private static final long serialVersionUID = 1L;

	public CommunicationException(String message) {
		super(message);
	}

	public CommunicationException(String message, Exception e) {
		super(message, e);
	}
}
