/**
 * 
 */
package com.uis.iot.common.gateway.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the {@link Storage} as a Data Transfer Object
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

	/**
	 * Gateway's id.
	 */
	private Long gatewayId;

	/**
	 * The process' id.
	 */
	private Long processId;

	/**
	 * The payload to be stored.
	 */
	private String payload;

	/**
	 * Represents the datetime when the message was emitted.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timestamp;

	public MessageDTO() {
		super();
	}

	public MessageDTO(Long gatewayId, Long processId, String payload, Date timestamp) {
		super();
		this.gatewayId = gatewayId;
		this.processId = processId;
		this.payload = payload;
		this.timestamp = timestamp;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
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

	@Override
	public String toString() {
		return "[gatewayId=" + gatewayId + ", processId=" + processId + ", payload=" + payload + "]";
	}
}
