package com.uis.iot.common.model;

/**
 * Data Transfer Object for a simple response message.
 * 
 * @author felipe.estupinan
 *
 */
public class TopicDTO {
	private String processId;
	private String value;	
	
	public TopicDTO() {
		super();
	}

	public TopicDTO(String processId, String value) {
		this.processId = processId;
		this.value = value;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
