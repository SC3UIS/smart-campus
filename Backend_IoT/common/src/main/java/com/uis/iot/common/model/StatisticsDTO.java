package com.uis.iot.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatisticsDTO {

	private long gatewaysAlive;

	private long gatewaysDeath;

	private long processesAlive;

	private long processesDeath;

	private long devices;

	private long applications;

	private List<StatusChangeDTO> changes;

	public StatisticsDTO() {
		super();
	}

	public long getGatewaysAlive() {
		return gatewaysAlive;
	}

	public void setGatewaysAlive(long gatewaysAlive) {
		this.gatewaysAlive = gatewaysAlive;
	}

	public long getGatewaysDeath() {
		return gatewaysDeath;
	}

	public void setGatewaysDeath(long gatewaysDeath) {
		this.gatewaysDeath = gatewaysDeath;
	}

	public long getProcessesAlive() {
		return processesAlive;
	}

	public void setProcessesAlive(long processesAlive) {
		this.processesAlive = processesAlive;
	}

	public long getProcessesDeath() {
		return processesDeath;
	}

	public void setProcessesDeath(long processesDeath) {
		this.processesDeath = processesDeath;
	}

	public long getDevices() {
		return devices;
	}

	public void setDevices(long devices) {
		this.devices = devices;
	}

	public long getApplications() {
		return applications;
	}

	public void setApplications(long applications) {
		this.applications = applications;
	}

	public List<StatusChangeDTO> getChanges() {
		return changes;
	}

	public void setChanges(List<StatusChangeDTO> changes) {
		this.changes = changes;
	}

}
