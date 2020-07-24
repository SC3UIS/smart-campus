package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when trying to insert a Process that already exists.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class ProcessAlredyExistException extends DomainException {

	private static final long serialVersionUID = 209413139342679142L;

	public ProcessAlredyExistException(String message) {
		super(message);
	}
	
	public ProcessAlredyExistException() {
		super("The given process already exists.");
	}
}
