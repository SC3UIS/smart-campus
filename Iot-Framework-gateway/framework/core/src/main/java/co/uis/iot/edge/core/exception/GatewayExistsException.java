package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when trying to insert another Gateway.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class GatewayExistsException extends DomainException {

	private static final long serialVersionUID = 7138593424708413994L;

	public GatewayExistsException(String message) {
		super(message);
	}
	
	public GatewayExistsException() {
		super("A gateway already exists.");
	}
}
