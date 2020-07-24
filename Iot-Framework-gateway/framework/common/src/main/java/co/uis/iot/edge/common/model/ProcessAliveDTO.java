/**
 * 
 */
package co.uis.iot.edge.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This object indicates if a process is working or not.
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessAliveDTO {

	/**
	 * The stored id in the Backend solution.
	 */
	private Long id;

	/**
	 * Indicates if the process is working or not.
	 */
	private boolean isAlive;

	/**
	 * Useful if the user wants to send why the process is failing.
	 */
	private String message;

	public ProcessAliveDTO() {
		super();
	}

	public ProcessAliveDTO(Long id, Boolean isAlive) {
		super();
		this.id = id;
		this.isAlive = isAlive;
	}
	
	public ProcessAliveDTO(Boolean isAlive, String message) {
		super();
		this.isAlive = isAlive;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsAlive() {
		return isAlive;
	}

	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", isAlive=" + isAlive + "]";
	}

}
