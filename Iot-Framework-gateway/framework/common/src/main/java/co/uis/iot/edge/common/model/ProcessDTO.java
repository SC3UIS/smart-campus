package co.uis.iot.edge.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the {@link Process} as a Data Transfer Object
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDTO {

	/**
	 * The stored id in the Backend solution.
	 */
	private Long id;

	/**
	 * The name of the process.
	 */
	private String name;

	/**
	 * The process' description.
	 */
	private String description;

	/**
	 * Indicates if the process was already deployed at least once.
	 */
	private boolean deployed;

	/**
	 * A List of {@link Property}.
	 */
	private List<PropertyDTO> properties;

	public ProcessDTO() {
		super();
	}

	public ProcessDTO(Long id, String name, String description, boolean deployed, List<PropertyDTO> properties) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.deployed = deployed;
		this.properties = properties;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	public List<PropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyDTO> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name + "]";
	}
}
