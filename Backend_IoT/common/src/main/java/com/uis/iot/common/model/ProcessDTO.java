package com.uis.iot.common.model;

import java.util.List;

import org.springframework.core.convert.Property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the {@link Process} as a Data Transfer Object
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
	 * The process' gateway Id.
	 */
	private Long gatewayId;

	/**
	 * Indicates if the process is alive or dead.
	 */
	private boolean alive;

	/**
	 * A List of {@link Property}.
	 */
	private List<ProcessPropertyDTO> properties;

	public ProcessDTO() {
		super();
	}

	public ProcessDTO(Long id, String name, Long gatewayId, String description, boolean alive) {
		this.id = id;
		this.name = name;
		this.gatewayId = gatewayId;
		this.description = description;
		this.alive = alive;
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

	public List<ProcessPropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<ProcessPropertyDTO> properties) {
		this.properties = properties;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
