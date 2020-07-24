package com.uis.iot.common.model;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * Represents the ProcessProperty as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 *
 */
public class ProcessPropertyDTO {

	private EPropertyType type;

	private Long processId;

	private String name;

	private String value;

	public ProcessPropertyDTO() {
		super();
	}

	public ProcessPropertyDTO(EPropertyType type, Long processId, String name, String value) {
		this.type = type;
		this.processId = processId;
		this.name = name;
		this.value = value;
	}

	public EPropertyType getType() {
		return type;
	}

	public void setType(EPropertyType type) {
		this.type = type;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long idGateway) {
		this.processId = idGateway;
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
