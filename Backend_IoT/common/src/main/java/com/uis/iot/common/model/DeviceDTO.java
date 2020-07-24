package com.uis.iot.common.model;

import java.util.List;

import com.uis.iot.common.gateway.model.EThingType;

/**
 * Represents the Device as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 *
 */
public class DeviceDTO {

	private Long id;

	private String name;

	private String description;

	private long gatewayId;

	private EThingType type;

	private List<DevicePropertyDTO> properties;
	
	public DeviceDTO() {
	}

	public DeviceDTO(final Long id, final String name, final long gatewayId, final EThingType type, final String description) {
		this.id = id;
		this.name = name;
		this.gatewayId = gatewayId;
		this.type = type;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(final long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public EThingType getType() {
		return type;
	}

	public void setType(final EThingType type) {
		this.type = type;
	}

	public List<DevicePropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(final List<DevicePropertyDTO> properties) {
		this.properties = properties;
	}

}
