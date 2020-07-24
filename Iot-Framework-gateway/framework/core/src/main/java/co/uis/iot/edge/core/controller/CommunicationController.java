package co.uis.iot.edge.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.ProcessAliveDTO;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.core.service.IProcessService;
import co.uis.iot.edge.core.utils.PropertyUtil;
import co.uis.iot.edge.core.vertx.IVertxHandler;

/**
 * Entry point for all Communication related REST services.
 * 
 * @author Camilo Gutiérrez
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/communication")
public class CommunicationController {
		
	@Autowired
	private IProcessService processService;
	
	@Autowired
	private IVertxHandler vertxHandler;
	
	/**
	 * Send messages to the given process by its topic.
	 * 
	 * @param messageDTO - message to be sent.
	 * @return the status code that determines whether the operation was successful
	 *         or not.
	 */
	@PostMapping(value = "/process/message", produces = "application/json", consumes = "application/json")
	public ResponseEntity<HttpStatus> sendMessageToProcess(@RequestBody MessageDTO messageDTO) {
		Assert.notNull(messageDTO.getProcessId(), "Process id is required.");
		Assert.hasText(messageDTO.getPayload(), "Payload is required.");
		
		String processTopic = PropertyUtil.getTopicIfExists(processService.getProcessByIdBackend(messageDTO.getProcessId()));
		Assert.notNull(processTopic, "The process' topic is empty or null");
		
		vertxHandler.publishToTopic(processTopic, messageDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Indicates if the gateway and the process are currently working or not.
	 * If this endpoint responds the caller has to assume that the gateway is alive. 
	 * 
	 * @return the list of processes with its state (alive or dead).
	 */
	@GetMapping(value = "/keepAlive", produces = "application/json")
	public ResponseEntity<List<ProcessAliveDTO>> getArchitectureStatus() {
		List<ProcessDTO> processDTOs = processService.getProcesses();
		List<ProcessAliveDTO> processAliveDTOs = new ArrayList<>();
		ProcessAliveDTO processAliveDTO;
		for (ProcessDTO processDTO: processDTOs) {
			processAliveDTO = vertxHandler.checkProcessAlive("keepAlive/" + processDTO.getId(), "{}");
			processAliveDTO.setId(processDTO.getId());
			processAliveDTOs.add(processAliveDTO);
		}
	
		return new ResponseEntity<>(processAliveDTOs, HttpStatus.OK);
	}
}
