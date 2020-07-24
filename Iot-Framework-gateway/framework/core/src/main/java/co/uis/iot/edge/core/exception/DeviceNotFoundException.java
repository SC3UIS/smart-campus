package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when a requested Device doesn't exist.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class DeviceNotFoundException extends DomainException {

	private static final long serialVersionUID = 4248328753582343932L;

	public DeviceNotFoundException(String message) {
		super(message);
	}
	
	public DeviceNotFoundException() {
		super("The requested device doesn't exist.");
	}

}
