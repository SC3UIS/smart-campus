package co.uis.iot.edge.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.core.service.IStorageService;

/**
 * Entry point for all Storage related REST services.
 * 
 * @author Camilo Gutérrez
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/storage")
public class StorageController {

	@Autowired
	private IStorageService service;

	/**
	 * Finds all Messages for a given process' id.
	 * 
	 * @param id of the Process.
	 * @return the list of matched messages.
	 */
	@GetMapping(value = "/messages", produces = "application/json")
	public ResponseEntity<List<MessageDTO>> getMessagesByProcessId(@RequestParam(required = true) Long processId) {
		return new ResponseEntity<>(service.getMessagesByProcessId(processId), HttpStatus.OK);
	}

	/**
	 * Delete messages by their process' id and sent status.
	 * 
	 * @param processId   the id in the backend of the Process.
	 * @param alreadySent messages' sent status.
	 * @return the status code that determines whether the operation was successful
	 *         or not.
	 */
	@DeleteMapping(value = "/messages")
	public ResponseEntity<Long> deleteMessages(@RequestParam long processId, @RequestParam boolean alreadySent) {
		service.deleteMessages(processId, alreadySent);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
