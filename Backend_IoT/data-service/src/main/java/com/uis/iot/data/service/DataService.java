package com.uis.iot.data.service;

import static com.uis.iot.common.utils.CommonUtils.getHTTPResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.uis.iot.common.gateway.model.MessageDTO;
import com.uis.iot.common.model.GatewayIpDTO;
import com.uis.iot.common.utils.ERoute;
import com.uis.iot.data.entity.Data;
import com.uis.iot.data.model.BroadcastMessageResponseDTO;
import com.uis.iot.data.model.StatisticDTO;
import com.uis.iot.data.repository.IDataRepository;
import com.uis.iot.data.repository.StatisticRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DataService implements IDataService {

	public DataService() {
	}
	
	@Autowired
	private IDataRepository dataRepository;
	
	@Autowired
	private StatisticRepository statisticRepository;
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<MessageDTO> getHistoricalData(Long userId, Long applicationId, String topic, Long gatewayId, Long processId, Date initialDate, Date endDate) {
		List<Long> processes = new ArrayList<>();
		String urlRequest;
		if (userId != null) {
			urlRequest = "userId=" + userId;
		} else if (applicationId != null) {
			urlRequest = "applicationId=" + applicationId;
		} else if (topic != null) {
			urlRequest = "topic=" + topic;
		} else if (gatewayId != null) {
			urlRequest = "gatewayId=" + gatewayId;
		} else {
			urlRequest = "processId=" + processId;
		}
		
		//Gets the processes Ids from the admin microservice
		processes = getHTTPResponse(ERoute.ADMIN + "processes/process/ids?" + urlRequest, HttpMethod.GET, List.class).getBody();
		if (initialDate != null && endDate != null) {
			final Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendarStart.setTime(initialDate);
				
			@SuppressWarnings("static-access")
			int hours = calendarStart.get(calendarStart.HOUR_OF_DAY);
			initialDate.setHours(hours);
			endDate.setHours(hours);
			return dataRepository.findByProcessIdInAndTimestampBetween(processes, initialDate, endDate).stream().map(Data::toDTO).collect(Collectors.toList());
		} else {
			return dataRepository.findByProcessIdIn(processes).stream().map(Data::toDTO).collect(Collectors.toList());
		}	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<BroadcastMessageResponseDTO> broadcastExternalApplicationMessage(List<Long> processIds, String topic, String message) {
		ResponseEntity<List> apiResponse = null;
		Date date = new Date();
		List<BroadcastMessageResponseDTO> response = new ArrayList<>();
		List<GatewayIpDTO> gateways = null;
		if (processIds == null || processIds.size() == 0) {
			apiResponse = getHTTPResponse(ERoute.ADMIN + "gateways/ip/topic?topic=" + topic, HttpMethod.GET, List.class);
		} else {
			apiResponse = getHTTPResponse(ERoute.ADMIN + "gateways/ip/process", HttpMethod.PUT, List.class, processIds);	
		}
		gateways = (List<GatewayIpDTO>) apiResponse.getBody()
					.stream().map(gateway -> 
						GatewayIpDTO.fromLinkedHashMap((LinkedHashMap) gateway))
					.collect(Collectors.toList());
		for (GatewayIpDTO gateway: gateways) {
			for (Long processId: gateway.getProcesses()) {
				MessageDTO messageDTO = new MessageDTO();
				messageDTO.setPayload(message);
				messageDTO.setProcessId(processId);
				messageDTO.setTimestamp(date);
				try {
					String ip = gateway.getIp() + "/communication/process/message";
					getHTTPResponse(ip, HttpMethod.POST, HttpStatus.class, messageDTO);
				} catch (Exception e) {
					response.add(new BroadcastMessageResponseDTO(
							gateway.getIp(), 
							e.getMessage()
					));
				}
			}
		}
		return response;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<StatisticDTO> getStatistics(String userId) {
		List<LinkedHashMap> gateways = getHTTPResponse(ERoute.ADMIN + "gateways/user/" + userId, HttpMethod.GET, List.class).getBody();
		List<Integer> gatewaysId = new ArrayList<>();
		List<Integer> processesId = new ArrayList<>(); 
		for (LinkedHashMap gateway: gateways) {
			Integer gatewayId = (Integer) gateway.get("id");
			gatewaysId.add(gatewayId);
		}
		List<LinkedHashMap> processes = getHTTPResponse(ERoute.ADMIN + "processes/user/" + userId, HttpMethod.GET, List.class).getBody();
		for (LinkedHashMap process: processes) {
			Integer processId = (Integer) process.get("id");
			processesId.add(processId);
		}
		return statisticRepository.getStatistics(gatewaysId, processesId);
	}
	
}
