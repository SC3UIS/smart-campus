package com.uis.iot.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * The primary key class for the device_property database table.
 * 
 */
@Embeddable
public class DevicePropertyPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String name;

	@Column(name = "id_device", nullable = false)
	private Long deviceId;

	@Enumerated(EnumType.STRING)
	@Column(name = "property_type", nullable = false)
	private EPropertyType namePropertyType;

	public DevicePropertyPK() {
		super();
	}

	public DevicePropertyPK(Long deviceId, EPropertyType propertyType, String name) {
		this.deviceId = deviceId;
		this.namePropertyType = propertyType;
		this.name = name;
	}

	public Long getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public EPropertyType getNamePropertyType() {
		return this.namePropertyType;
	}

	public void setNamePropertyType(EPropertyType namePropertyType) {
		this.namePropertyType = namePropertyType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DevicePropertyPK)) {
			return false;
		}
		DevicePropertyPK castOther = (DevicePropertyPK) other;
		return this.deviceId.equals(castOther.deviceId) && this.namePropertyType.equals(castOther.namePropertyType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.deviceId.hashCode();
		hash = hash * prime + this.namePropertyType.hashCode();

		return hash;
	}
}