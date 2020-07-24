package com.uis.iot.data.service;

import java.util.Date;
import java.util.List;

import com.uis.iot.common.gateway.model.MessageDTO;
import com.uis.iot.data.model.BroadcastMessageResponseDTO;
import com.uis.iot.data.model.StatisticDTO;

public interface IDataService {
    
	/**
	 * Broadcasts a message to the gateways that have the targeted actuators.
	 * 
	 * @param topic topic of the processes the message will be sent to.
	 * @param processIds Ids of the processes the message will be sent to.
	 * @param message message to be published.
	 * @return a list of {@link BroadcastMessageResponseDTO} that indicates the gateways that did not receive the message, if it returns an empty list it means all the receiver actuators succesfully received the message.
	 */
	public List<BroadcastMessageResponseDTO> broadcastExternalApplicationMessage(List<Long> processIds, String topic, String message);
	
	/**
	 * Returns the messages stored in the selected processes between the indicated dates.
	 * 
	 * @param userId id of the user which processes data will be returned. 
	 * @param applicationId id of the application which processes data will be returned. 
	 * @param topic topic of the processes which data will be returned.
	 * @param gatewayId id of the gateway which processes data will be returned. 
	 * @param processId id of the process which data will be returned. 
	 * @param initialDate the start date of the returned data, it goes in this format: yyyy-MM-dd, optional.
	 * @param initialDate the start date of the returned data, it goes in this format: yyyy-MM-dd, optional.
	 * @return a list of {@link MessageDTO} of the requested processes on the requested dates.
	 */
	public List<MessageDTO> getHistoricalData(Long userId, Long applicationId, String topic, Long gatewayId, Long processId, Date initialDate, Date endDate);

	public List<StatisticDTO> getStatistics(String userId);
}
