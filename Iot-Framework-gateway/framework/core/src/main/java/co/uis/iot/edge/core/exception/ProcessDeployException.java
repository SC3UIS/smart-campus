package co.uis.iot.edge.core.exception;

/**
 * Exception thrown when an error occurred deploying/undeploying a JAR
 * 
 * @author Camilo Gutiérrez
 *
 */
public class ProcessDeployException extends DomainException {

	private static final long serialVersionUID = 1L;

	public ProcessDeployException(Throwable t) {
		super(t);
	}
}
