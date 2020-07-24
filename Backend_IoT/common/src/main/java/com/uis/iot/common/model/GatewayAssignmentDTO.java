package com.uis.iot.common.model;

/**
 * Represents the Gateway as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 * @author Kevin
 *
 */
public class GatewayAssignmentDTO {

	private Long gatewayId;
	private Long applicationId;
	
	
	public Long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	
}
