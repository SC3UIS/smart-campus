package com.uis.iot.common.gateway.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the Registry as a Data Transfer Object
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistryDTO {

	private Long id;

	private String name;

	private String description;

	private List<PropertyDTO> properties;

	private EThingType type;

	private Boolean successful;

	private String message;

	public RegistryDTO() {
		super();
	}

	public RegistryDTO(Long id, String name, List<PropertyDTO> properties, EThingType type, Boolean successful,
			String message, String description) {
		super();
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.type = type;
		this.successful = successful;
		this.message = message;
		this.description = description;
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

	public Boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EThingType getType() {
		return type;
	}

	public void setType(EThingType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name + ", description=" + description + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof RegistryDTO) {
			RegistryDTO other = (RegistryDTO) obj;
			return Objects.equals(id, other.getId()) && Objects.equals(name, other.getName())
					&& Objects.equals(properties, other.getProperties()) && type == other.getType();
		}
		return false;
	}

}
