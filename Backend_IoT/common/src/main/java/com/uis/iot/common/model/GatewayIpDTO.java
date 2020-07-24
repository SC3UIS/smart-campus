package com.uis.iot.common.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Data Transfer Object for REST communication with the Gateway.
 * 
 * @author felipe.estupinan
 * @author Kevin
 *
 */
public class GatewayIpDTO {

	private String ip;

	private List<Long> processes;

	public GatewayIpDTO(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<Long> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Long> processes) {
		this.processes = processes;
	}

	@SuppressWarnings("unchecked")
	public static GatewayIpDTO fromLinkedHashMap(LinkedHashMap<String, Object> map) {
		String ip = (String) map.get("ip");
		GatewayIpDTO gatewayIpDto = new GatewayIpDTO(ip);
		if (map.get("processes") != null) {
			List<Integer> processes = (List<Integer>) map.get("processes");
			gatewayIpDto.setProcesses(new ArrayList<>());
			for(Integer process: processes) {
				gatewayIpDto.getProcesses().add(Long.valueOf(process));
			}
		}
		return gatewayIpDto;
	}
}
