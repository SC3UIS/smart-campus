package com.uis.iot.data.model;

public class StatisticPkDTO {
	private Long gatewayId;
	private Long processId;
	private Integer hour;
	private Integer dayOfWeek;
	private Integer dayOfMonth;
	private Integer month;
	
	public Long getGatewayId() {
		return gatewayId;
	}
	
	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public Integer getHour() {
		return hour;
	}
	
	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Integer getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}
}
