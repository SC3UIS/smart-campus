package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when a requested Process doesn't exist.
 * 
 * @author Camilo Gutiérrez.
 *
 */
public class ProcessNotFoundException extends DomainException {

	private static final long serialVersionUID = 209413139342679142L;

	public ProcessNotFoundException(String message) {
		super(message);
	}
	
	public ProcessNotFoundException() {
		super("The requested/given process doesn't exist.");
	}

}
