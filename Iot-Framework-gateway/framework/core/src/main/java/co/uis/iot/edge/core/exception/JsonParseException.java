package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when a problem occurred communicating with a MQTT broker.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class JsonParseException extends DomainException {

	private static final long serialVersionUID = 1L;

	public JsonParseException(String message) {
		super(message);
	}

	public JsonParseException(String message, Exception e) {
		super(message, e);
	}
}
