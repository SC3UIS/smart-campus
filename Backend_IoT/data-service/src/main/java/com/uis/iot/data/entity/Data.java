package com.uis.iot.data.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.uis.iot.common.gateway.model.MessageDTO;

@Document(collection = "data")
public class Data {

	@Id
	private String id;
	
	@Indexed(unique = false)
	private Long processId;
	
	private Long gatewayId;
	
	private String message;
	
	private Date timestamp;

	public Data() {
		super();
	}

	public Data(String id, Long processId, Long gatewayId, String message, Date timestamp) {
		super();
		this.processId = processId;
		this.gatewayId = gatewayId;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public MessageDTO toDTO() {
		return new MessageDTO(gatewayId, processId, message, timestamp);
	}

	public static Data fromDTO(MessageDTO dto) {
		return new Data(null, dto.getProcessId(), dto.getGatewayId(), dto.getPayload(), dto.getTimestamp());
	}	
}
