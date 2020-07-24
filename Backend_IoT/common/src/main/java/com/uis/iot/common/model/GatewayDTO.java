package com.uis.iot.common.model;

import java.util.List;

/**
 * Represents the Gateway as a Data Transfer Object.
 * 
 * @author felipe.estupinan
 * @author Kevin
 *
 */
public class GatewayDTO {

	private Long id;

	private String ip;

	private String name;

	private String description;

	private List<GatewayPropertyDTO> properties;

	private List<DeviceDTO> devices;

	private List<ProcessDTO> processes;

	private List<ApplicationDTO> applications;

	private boolean alive;
	
	private Long userId;

	public GatewayDTO() {
		super();
	}

	public GatewayDTO(Long id, String ip, String description, String name, boolean alive) {
		this.id = id;
		this.ip = ip;
		this.description = description;
		this.name = name;
		this.alive = alive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<GatewayPropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<GatewayPropertyDTO> properties) {
		this.properties = properties;
	}

	public List<DeviceDTO> getDevices() {
		return devices;
	}

	public void setDevices(List<DeviceDTO> devices) {
		this.devices = devices;
	}

	public List<ProcessDTO> getProcesses() {
		return processes;
	}

	public void setProcesses(List<ProcessDTO> processes) {
		this.processes = processes;
	}

	public List<ApplicationDTO> getApplications() {
		return applications;
	}

	public void setApplications(List<ApplicationDTO> applications) {
		this.applications = applications;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}	
}
