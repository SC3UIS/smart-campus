package com.uis.iot.common.model;

import java.util.List;

/**
 * Represents the Application as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 *
 */
public class ApplicationDTO {

	private Long id;

	private String name;

	private Long userId;

	private String description;

	private List<GatewayDTO> gateways;

	public ApplicationDTO() {
		super();
	}

	public ApplicationDTO(Long id, String name, Long userId, String description, List<GatewayDTO> gateways) {
		this.id = id;
		this.name = name;
		this.userId = userId;
		this.description = description;
		this.gateways = gateways;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public List<GatewayDTO> getGateways() {
		return gateways;
	}

	public void setGateways(List<GatewayDTO> gateways) {
		this.gateways = gateways;
	}

}
