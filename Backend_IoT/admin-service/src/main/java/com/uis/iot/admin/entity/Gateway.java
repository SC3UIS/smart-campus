package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.uis.iot.common.gateway.model.EThingType;
import com.uis.iot.common.gateway.model.PropertyDTO;
import com.uis.iot.common.gateway.model.RegistryDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.GatewayIpDTO;
import com.uis.iot.common.model.GatewayPropertyDTO;

/**
 * The persistent class for the gateway database table.
 * 
 */
@Entity
@NamedQuery(name = "Gateway.findAll", query = "SELECT g FROM Gateway g")
public class Gateway implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_gateway")
	private Long id;

	@Column(name = "ip_gateway", nullable = false)
	private String ipGateway;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(name = "is_alive")
	private boolean alive;

	// bi-directional many-to-many association to Application
	@ManyToMany
	@JoinTable(name = "application_per_gateway", joinColumns = {
			@JoinColumn(name = "id_gateway") }, inverseJoinColumns = { @JoinColumn(name = "id_application") })
	private List<Application> applications;

	// bi-directional many-to-one association to Device
	@OneToMany(mappedBy = "gateway")
	private List<Device> devices;

	// bi-directional many-to-one association to Process
	@OneToMany(mappedBy = "gateway")
	private List<Process> processes;

	// bi-directional many-to-one association to Notification
	@OneToMany(mappedBy = "gateway")
	private List<Notification> notifications;

	// bi-directional many-to-one association to GatewayProperty
	@OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL, targetEntity = GatewayProperty.class, orphanRemoval = true)
	private List<GatewayProperty> gatewayProperties;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_user")
	private AppUser user;

	public Gateway() {
		super();
	}

	public Gateway(final Long id, final String ipGateway, final String description, final String name, final boolean alive) {
		this.id = id;
		this.ipGateway = ipGateway;
		this.description = description;
		this.name = name;
		this.alive = alive;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long gatewayId) {
		this.id = gatewayId;
	}

	public String getIpGateway() {
		return this.ipGateway;
	}

	public void setIpGateway(final String ipGateway) {
		this.ipGateway = ipGateway;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(final boolean alive) {
		this.alive = alive;
	}

	public List<Application> getApplications() {
		if (this.applications == null) {
			this.applications = new ArrayList<>(1);
		}
		return this.applications;
	}

	public void setApplications(final List<Application> applications) {
		this.applications = applications;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(final List<Device> devices) {
		this.devices = devices;
	}

	public Device addDevice(final Device device) {
		getDevices().add(device);
		device.setGateway(this);

		return device;
	}

	public Device removeDevice(final Device device) {
		getDevices().remove(device);
		device.setGateway(null);

		return device;
	}

	public List<Process> getProcesses() {
		return this.processes;
	}

	public void setProcesses(final List<Process> processes) {
		this.processes = processes;
	}

	public List<GatewayProperty> getGatewayProperties() {
		return this.gatewayProperties;
	}

	public void setGatewayProperties(final List<GatewayProperty> gatewayProperties) {
		this.gatewayProperties = gatewayProperties;
	}

	public GatewayProperty addGatewayProperty(final GatewayProperty gatewayProperty) {
		getGatewayProperties().add(gatewayProperty);
		gatewayProperty.setGateway(this);
		return gatewayProperty;
	}

	public GatewayProperty removeGatewayProperty(final GatewayProperty gatewayProperty) {
		getGatewayProperties().remove(gatewayProperty);
		gatewayProperty.setGateway(null);

		return gatewayProperty;
	}

	public List<Notification> getNotifications() {
		if (notifications == null) {
			notifications = new ArrayList<>(1);
		}
		return notifications;
	}

	public void setNotifications(final List<Notification> notifications) {
		this.notifications = notifications;
	}
	
	public AppUser getUser() {
		return user;
	}

	public void setUser(final AppUser user) {
		this.user = user;
	}

	/**
	 * @param gatewayDTO
	 * @return new Device entity.
	 */
	public static Gateway fromDTO(final GatewayDTO gatewayDTO) {
		final Gateway gateway = new Gateway(gatewayDTO.getId(), gatewayDTO.getIp(), gatewayDTO.getDescription(),
				gatewayDTO.getName(), gatewayDTO.isAlive());
		gateway.setGatewayProperties(new ArrayList<>());
		gateway.setUser(new AppUser(gatewayDTO.getUserId()));
		if (gatewayDTO.getProperties() != null) {
			for (final GatewayPropertyDTO propDTO : gatewayDTO.getProperties()) {
				propDTO.setGatewayId(gatewayDTO.getId() != null ? gatewayDTO.getId() : null);
				gateway.addGatewayProperty(GatewayProperty.fromDTO(propDTO));
			}
		}
		return gateway;
	}

	/**
	 * @return dto of the entity.
	 */
	public GatewayDTO toDTO() {
		final GatewayDTO gatewayDTO = new GatewayDTO(id, ipGateway, description, name, alive);
		gatewayDTO.setProperties(gatewayProperties != null ? gatewayProperties.stream().map(GatewayProperty::toDTO).collect(Collectors.toList()) : null);
		gatewayDTO.setDevices(devices != null ? devices.stream().map(Device::toDTO).collect(Collectors.toList()) : null);
		gatewayDTO.setProcesses(processes != null ? processes.stream().map(Process::toDTO).collect(Collectors.toList()) : null);
		gatewayDTO.setUserId(user != null ? user.getId() : null);
		return gatewayDTO;
	}
	
	/**
	 * 
	 * @param withApplications indicates if the GatewayDTO has to contains the applications it is related to.
	 * @return dto of the entity.
	 */
	public GatewayDTO toDTO(final boolean withApplications) {
		final GatewayDTO gatewayDTO = toDTO();
		if (withApplications) {
			gatewayDTO.setApplications(applications != null ? applications.stream().map(Application::toDTO).collect(Collectors.toList()) : null);
		}
		return gatewayDTO;
	}
	
	/**
	 * @return IP dto of the entity.
	 */
	public GatewayIpDTO toIpDTO() {
		final GatewayIpDTO gatewayDTO = new GatewayIpDTO(ipGateway);
		gatewayDTO.setProcesses(processes != null ? processes.stream().map(Process::getId).collect(Collectors.toList()) : null);
		return gatewayDTO;
	}

	public RegistryDTO toDTOforGateway() {
		final List<PropertyDTO> properties = gatewayProperties.stream().map(GatewayProperty::toDTOforGateway)
				.collect(Collectors.toList());
		return new RegistryDTO(id, name, properties, EThingType.GATEWAY, null, null, description);
	}

	public static Gateway fromDTOfromGateway(final RegistryDTO gatewayDTO, final String ipGateway, final AppUser user) {
		final Gateway gateway = new Gateway(gatewayDTO.getId(), ipGateway, gatewayDTO.getDescription(),
				gatewayDTO.getName(), true);
		gateway.setGatewayProperties(gatewayDTO.getProperties().stream()
				.map(property -> {
					final GatewayProperty fromDTOfromGateway = GatewayProperty.fromDTOfromGateway(property, gatewayDTO.getId());
					fromDTOfromGateway.setGateway(gateway);
					return fromDTOfromGateway;
				})
				.collect(Collectors.toList()));
		gateway.setUser(user);
		return gateway;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Gateway) {
			final Gateway other = (Gateway) obj;
			if (id != null) {
				return Objects.equals(id, other.getId());
			} else {
				return Objects.equals(name, other.getName()) && Objects.equals(description, other.getDescription())
						&& Objects.equals(ipGateway, other.getIpGateway());
			}
		}
		return false;
	}

}