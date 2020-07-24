package com.uis.iot.common.gateway.model;

import java.util.List;

import org.springframework.core.convert.Property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the {@link Process} as a Data Transfer Object
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
	 * A List of {@link Property}.
	 */
	private List<PropertyDTO> properties;

	public ProcessDTO() {
		super();
	}

	public ProcessDTO(Long id, String name, String description, List<PropertyDTO> properties) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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
