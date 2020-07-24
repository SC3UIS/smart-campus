package com.uis.iot.common.model;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * Represents the Device as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 *
 */
public class DevicePropertyDTO {

	private EPropertyType type;

	private long deviceId;

	private String name;

	private String value;

	public DevicePropertyDTO() {
		super();
	}

	public DevicePropertyDTO(EPropertyType type, long deviceId, String name, String value) {
		this.type = type;
		this.deviceId = deviceId;
		this.name = name;
		this.value = value;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public EPropertyType getType() {
		return type;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
