package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when trying to insert a Device that already exists.
 * 
 * @author Camilo Gutiérrez.
 *
 */
public class DeviceAlreadyExistsException extends DomainException {

	private static final long serialVersionUID = -4538659273025659696L;

	public DeviceAlreadyExistsException(String message) {
		super(message);
	}

	public DeviceAlreadyExistsException() {
		super("A device with the same id and type already exists.");
	}

}
