package com.uis.iot.data.controller;

import java.util.Date;
import java.util.List;

import com.uis.iot.common.gateway.model.MessageDTO;
import com.uis.iot.data.model.BroadcastMessageDTO;
import com.uis.iot.data.model.BroadcastMessageResponseDTO;
import com.uis.iot.data.model.StatisticDTO;
import com.uis.iot.data.service.IActiveMQService;
import com.uis.iot.data.service.IDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for all data related REST services.
 * 
 * @author RoJo
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
public class DataController {

    @Autowired
    private IDataService service;
    
    @Autowired
    private IActiveMQService activeMQService;
    
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
	 * @return a list of {@link MessageDTO} of the requested processes data on the requested dates.
	 */
	@GetMapping(path = "/historic", produces = "application/json")
	public ResponseEntity<List<MessageDTO>> getHistoricalData(@RequestParam(value = "userId", required = false) Long userId,
													@RequestParam(value = "applicationId", required = false) Long applicationId,
            										@RequestParam(value = "topic", required = false) String topic,
            										@RequestParam(value = "gatewayId", required = false) Long gatewayId, 
            										@RequestParam(value = "processId", required = false) Long processId,
            										@RequestParam(value = "initialDate", required = false) 
            										@DateTimeFormat(pattern="yyyy-MM-dd") Date initialDate,
            										@RequestParam(value = "endDate", required = false)
													@DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        Assert.isTrue(userId != null || applicationId != null || topic != null || gatewayId != null || processId != null, "Alguno de los parametros de búsqueda debe ser enviado");
        return new ResponseEntity<>(service.getHistoricalData(userId, applicationId, topic, gatewayId, processId, initialDate, endDate), HttpStatus.OK);
	}
    
    /**
	 * Broadcasts a message to the gateways that have the targeted actuators.
	 * 
	 * @param topic topic of the processes the message will be sent to.
	 * @param processIds Ids of the processes the message will be sent to.
	 * @param message message to be published.
	 * @return a list of {@link BroadcastMessageResponseDTO} that indicates the gateways that did not receive the message, if it returns an empty list it means all the receiver actuators successfully received the message.
	 */
	@PostMapping(value = "/message")
	public ResponseEntity<List<BroadcastMessageResponseDTO>> broadcastExternalApplicationMessage(
            @RequestBody BroadcastMessageDTO request) {
		Assert.isTrue((request.getProcessIds() != null && request.getProcessIds().size() > 0 ) || request.getTopic() != null, "Alguno de los parametros de destino debe ser enviado.");
        Assert.hasText(request.getMessage(), "Payload requerido.");
        return new ResponseEntity<>(service.broadcastExternalApplicationMessage(request.getProcessIds(), request.getTopic(), request.getMessage()), HttpStatus.OK);
	}
	
	/**
	 * Subscribes the backend to a new topic when a gateway is added.
	 * 
	 * @param topic it is the gatewayId that was created.
	 * @return a {@link String} that indicates if the subscription succeeded or not.
	 */
	@GetMapping(path = "/subscribe/{topic}", produces = "application/json")
	public ResponseEntity<String> subscribe(@PathVariable final String topic) {
		return new ResponseEntity<>(activeMQService.subscribe(topic), HttpStatus.OK);
	}
	
	@GetMapping(path = "/statistics/{userId}", produces = "application/json")
	public ResponseEntity<List<StatisticDTO>> getStatistics(@PathVariable final String userId) {
		Assert.hasText(userId, "Id del usuario requerido.");
		return new ResponseEntity<List<StatisticDTO>>(service.getStatistics(userId), HttpStatus.OK);
	}
}