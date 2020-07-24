package com.uis.iot.common.model;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * Represents the Gateway as a Data Transfer Object.
 * 
 * @author Kevin
 *
 */
public class GatewayPropertyDTO {
	private EPropertyType type;
	private Long gatewayId;
	private String name;
	private String value;
	
	public GatewayPropertyDTO() {
	}
	
	public GatewayPropertyDTO(EPropertyType type, Long gatewayId, String name, String value) {
		this.type = type;
		this.gatewayId = gatewayId;
		this.name = name;
		this.value = value;
	}

	public EPropertyType getType() {
		return type;
	}

	public void setType(EPropertyType type) {
		this.type = type;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
