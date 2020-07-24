package com.uis.iot.data.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author kevin.arias
 * @author felipe.estupinan
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BroadcastMessageDTO {
	/**
	 * Message to be broadcasted
	 */
	private String message;
	
	/**
	 * Processes' list
	 */
	private List<Long> processIds;
	
	/**
	 * Topic
	 */
	private String topic;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Long> getProcessIds() {
		return processIds;
	}

	public void setProcessIds(List<Long> processIds) {
		this.processIds = processIds;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
