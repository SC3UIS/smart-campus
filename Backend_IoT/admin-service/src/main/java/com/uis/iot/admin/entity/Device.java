package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.springframework.util.CollectionUtils;

import com.uis.iot.common.gateway.model.EThingType;
import com.uis.iot.common.gateway.model.PropertyDTO;
import com.uis.iot.common.gateway.model.RegistryDTO;
import com.uis.iot.common.model.DeviceDTO;
import com.uis.iot.common.model.DevicePropertyDTO;

/**
 * The persistent class for the device database table.
 * 
 */
@Entity
@NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d")
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_device")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "device_type", nullable = false)
	private EThingType deviceType;

	// bi-directional many-to-one association to Gateway
	@ManyToOne
	@JoinColumn(name = "id_gateway")
	private Gateway gateway;

	// bi-directional many-to-one association to DeviceProperty
	@OneToMany(mappedBy = "device", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = DeviceProperty.class, orphanRemoval = true)
	private List<DeviceProperty> deviceProperties;

	public Device() {
		super();
	}

	public Device(final Long id, final String name, final EThingType deviceType, final Gateway gateway,
			final String description) {
		super();
		this.id = id;
		this.name = name;
		this.deviceType = deviceType;
		this.gateway = gateway;
		this.description = description;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long deviceId) {
		this.id = deviceId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public EThingType getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(final EThingType deviceType) {
		this.deviceType = deviceType;
	}

	public Gateway getGateway() {
		return this.gateway;
	}

	public void setGateway(final Gateway gateway) {
		this.gateway = gateway;
	}

	public List<DeviceProperty> getDeviceProperties() {
		return this.deviceProperties;
	}

	public void setDeviceProperties(final List<DeviceProperty> deviceProperties) {
		this.deviceProperties = deviceProperties;
	}

	public DeviceProperty addDeviceProperty(final DeviceProperty deviceProperty) {
		getDeviceProperties().add(deviceProperty);
		deviceProperty.setDevice(this);

		return deviceProperty;
	}

	public DeviceProperty removeDeviceProperty(final DeviceProperty deviceProperty) {
		getDeviceProperties().remove(deviceProperty);
		deviceProperty.setDevice(null);

		return deviceProperty;
	}

	/**
	 * @param deviceDTO
	 * @return new Device entity.
	 */
	public static Device fromDTO(final DeviceDTO deviceDTO) {
		final Device device = new Device(deviceDTO.getId(), deviceDTO.getName(), deviceDTO.getType(),
				new Gateway(deviceDTO.getGatewayId(), null, null, null, true), deviceDTO.getDescription());
		device.setDeviceProperties(new ArrayList<>());
		if (!CollectionUtils.isEmpty(deviceDTO.getProperties())) {
			for (DevicePropertyDTO propDTO : deviceDTO.getProperties()) {
				device.addDeviceProperty(DeviceProperty.fromDTO(propDTO));
			}
		}
		return device;
	}

	/**
	 * @return dto of the entity.
	 */
	public DeviceDTO toDTO() {
		final DeviceDTO deviceDTO = new DeviceDTO(id, name, gateway.getId(), deviceType, description);
		if (deviceProperties != null && !deviceProperties.isEmpty()) {
			final List<DevicePropertyDTO> properties = new ArrayList<>();
			for (DeviceProperty prop : deviceProperties) {
				properties.add(prop.toDTO());
			}
			deviceDTO.setProperties(properties);
		}
		return deviceDTO;
	}

	/**
	 * @return dto of the entity without properties.
	 */
	public DeviceDTO toDTO(boolean withProperties) {
		DeviceDTO deviceDTO = this.toDTO();
		if (withProperties) {
			deviceDTO.setProperties(null);
		}
		return deviceDTO;
	}

	/**
	 * @return
	 */
	public RegistryDTO toDTOforGateway() {
		final List<PropertyDTO> props = new ArrayList<>();
		for (final DeviceProperty prop : getDeviceProperties()) {
			props.add(prop.toDTOforGateway());
		}
		return new RegistryDTO(id, name, props, deviceType, null, null, description);
	}

	/**
	 * @param deviceDTOafterGateway
	 * @return
	 */
	public static Device fromDTOfromGateway(final RegistryDTO deviceDTO, final long idGateway) {
		final Gateway gateway = new Gateway();
		gateway.setId(idGateway);

		final Device device = new Device(deviceDTO.getId(), deviceDTO.getName(), deviceDTO.getType(), gateway,
				deviceDTO.getDescription());

		final List<DeviceProperty> props = new ArrayList<>();
		for (final PropertyDTO prop : deviceDTO.getProperties()) {
			final DeviceProperty propertyFromGateway = DeviceProperty.fromDTOfromGateway(prop, deviceDTO.getId());
			propertyFromGateway.setDevice(device);
			props.add(propertyFromGateway);
		}

		device.setDeviceProperties(props);
		return device;
	}
}