package com.uis.iot.data.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the {@link Storage} as a Data Transfer Object
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayDataDTO {

	/**
	 * The process id.
	 */
	private Long processId;
	
	/**
	 * The gateway id.
	 */
	private Long gatewayId;

	/**
	 * The payload to be stored.
	 */
	private String payload;

	/**
	 * Represents the datetime when the message was emitted.
	 */
	private Date timestamp;

	public GatewayDataDTO() {
		super();
	}

	public GatewayDataDTO(Long processId, Long gatewayId, String payload, Date timestamp) {
		super();
		this.processId = processId;
		this.gatewayId = gatewayId;
		this.payload = payload;
		this.timestamp = timestamp;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	
	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}