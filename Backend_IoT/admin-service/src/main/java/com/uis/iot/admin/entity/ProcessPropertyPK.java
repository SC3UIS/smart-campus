package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * The primary key class for the process_property database table.
 * 
 */
@Embeddable
public class ProcessPropertyPK implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String name;

	@Column(name = "id_process")
	private Long processId;

	@Enumerated(EnumType.STRING)
	@Column(name = "name_property_type")
	private EPropertyType type;

	public ProcessPropertyPK() {
		super();
	}

	public ProcessPropertyPK(Long processId, EPropertyType type, String name) {
		this.processId = processId;
		this.type = type;
		this.name = name;
	}

	public Long getProcessId() {
		return this.processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public EPropertyType getType() {
		return this.type;
	}

	public void setType(EPropertyType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, processId, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof ProcessPropertyPK) {
			ProcessPropertyPK other = (ProcessPropertyPK) obj;
			return Objects.equals(name, other.getName()) && Objects.equals(processId, other.getProcessId())
					&& Objects.equals(other.getType(), type);
		}
		return false;
	}
}