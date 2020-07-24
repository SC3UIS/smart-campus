package com.uis.iot.admin.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.uis.iot.common.gateway.model.EPropertyType;
import com.uis.iot.common.gateway.model.PropertyDTO;
import com.uis.iot.common.model.DevicePropertyDTO;

/**
 * The persistent class for the device_property database table.
 * 
 */
@Entity
@Table(name = "device_property")
@NamedQuery(name = "DeviceProperty.findAll", query = "SELECT d FROM DeviceProperty d")
public class DeviceProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DevicePropertyPK id;

	@Column(nullable = false)
	private String value;

	// bi-directional many-to-one association to Device
	@MapsId("deviceId")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_device", referencedColumnName = "id_device")
	private Device device;

	public DeviceProperty() {
		super();
	}

	public DeviceProperty(EPropertyType propertyType, Long deviceId, String name, String value) {
		this.id = new DevicePropertyPK(deviceId, propertyType, name);
		this.value = value;
	}

	public DevicePropertyPK getId() {
		return this.id;
	}

	public void setId(DevicePropertyPK id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public static DeviceProperty fromDTO(DevicePropertyDTO propertyDTO) {
		return new DeviceProperty(propertyDTO.getType(), propertyDTO.getDeviceId(), propertyDTO.getName(),
				propertyDTO.getValue());
	}

	public DevicePropertyDTO toDTO() {
		return new DevicePropertyDTO(id.getNamePropertyType(), id.getDeviceId(), id.getName(), value);
	}

	public PropertyDTO toDTOforGateway() {
		return new PropertyDTO(id.getName(), value, id.getNamePropertyType());
	}

	public static DeviceProperty fromDTOfromGateway(PropertyDTO propertyDTO, Long idDevice) {
		return new DeviceProperty(propertyDTO.getType(), idDevice, propertyDTO.getName(), propertyDTO.getValue());
	}

}