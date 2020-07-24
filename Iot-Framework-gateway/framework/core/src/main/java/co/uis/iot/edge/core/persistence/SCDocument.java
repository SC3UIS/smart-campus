package co.uis.iot.edge.core.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Represents any Persisted Document (not Embedded).
 * 
 * @author Camilo Gutiérrez
 *
 */
public class SCDocument {

	/**
	 * The id used by Mongo.
	 */
	@Id
	protected String id;
	
	/**
	 * The id that represents the Object in the backend.
	 */
	@Indexed(unique = false)
	protected Long idBackend;
	
	public SCDocument() {
		super();
	}
	
	/**
	 * @return the id of the Document.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id of the Document.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id that represents the object in the Backend.
	 */
	public Long getIdBackend() {
		return idBackend;
	}

	/**
	 * @param idBackend that represents the object in the Backend.
	 */
	public void setIdBackend(Long idBackend) {
		this.idBackend = idBackend;
	}
}
