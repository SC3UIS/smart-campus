/**
 * 
 */
package co.uis.iot.edge.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Contains the data related to a response from the core to the processes.
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventBusResponseDTO {

	/**
	 * Indicates if the response was successful or not.
	 */
	private Boolean succeeded;
	
	
	/**
	 * Could contain the expected response or an error message.
	 */
	private String body;
		
	public EventBusResponseDTO() {
		super();
	}

	public EventBusResponseDTO(Boolean succeeded, String body) {
		super();
		this.succeeded = succeeded;
		this.body = body;
	}

	public Boolean getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(Boolean succeeded) {
		this.succeeded = succeeded;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "EventBusResponseDTO [succeeded=" + succeeded + ", body=" + body + "]";
	}
}
