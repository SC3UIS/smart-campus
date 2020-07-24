package co.uis.iot.edge.core.exception;

/**
 * Exception used when a general Persistence error occurred.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class PersistenceException extends RuntimeException {

	private static final long serialVersionUID = 209413139342679142L;
	
	public PersistenceException(String message, Exception e) {
		super(message, e);
	}  
}
