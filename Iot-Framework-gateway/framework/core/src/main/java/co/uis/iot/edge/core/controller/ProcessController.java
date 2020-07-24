package co.uis.iot.edge.core.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.ResponseDTO;
import co.uis.iot.edge.core.service.IProcessService;

/**
 * Entry point for all process related REST services.
 * 
 * @author Camilo Gutiérrez
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/process")
public class ProcessController {

	@Autowired
	private IProcessService service;

	/**
	 * Finds all Processes or one by its id (backend).
	 * 
	 * @param id of the Process to be found. Use <code>null</code> to return all.
	 * @return the list of Processes.
	 */
	@GetMapping(value = "/processes", produces = "application/json")
	public ResponseEntity<List<ProcessDTO>> getProcesses(@RequestParam(required = false) Long id) {
		if (id == null) {
			return new ResponseEntity<>(service.getProcesses(), HttpStatus.OK);
		}
		return new ResponseEntity<>(Collections.singletonList(service.getProcessByIdBackend(id)), HttpStatus.OK);
	}

	/**
	 * Creates a process.
	 * 
	 * @param process the object to be created.
	 * @return the process if it was successfully created or an error otherwise.
	 */
	@PostMapping(value = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ProcessDTO> createProcess(@RequestBody ProcessDTO process) {
		Assert.notNull(process.getId(), "The process' id can't be null");
		Assert.notNull(process.getName(), "The process' name can't be null");
		return new ResponseEntity<>(service.createProcess(process), HttpStatus.OK);
	}

	/**
	 * Updates a process and its properties.
	 * 
	 * @param process the object to be updated.
	 * @return the process if it was successfully updated or an error otherwise.
	 */
	@PutMapping(value = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ProcessDTO> updateProcess(@RequestBody ProcessDTO processDTO) {
		return new ResponseEntity<>(service.updateProcess(processDTO), HttpStatus.OK);
	}

	/**
	 * Delete a process and their properties.
	 * 
	 * @param processId the id in the backend of the Process to be deleted.
	 * @return the status code that determines whether the operation was successful
	 *         or not.
	 */
	@DeleteMapping(value = "/{processId}")
	public ResponseEntity<HttpStatus> deleteProcess(@PathVariable long processId) {
		service.deleteProcess(processId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Deploys a given Process by its id. 
	 * If the PROCESS_JAR property is not set then nothing is done.
	 * 
	 * @param processId the id of the Process.
	 * @param deploy    <code>true</code> to deploy/redeploy the process,
	 *                  <code>false</code> to undeploy it.
	 *                  
	 * @return a {@link ResponseDTO} object that indicates if the JAR was deployed successfully or not.
	 */
	@PutMapping(value = "/{processId}/deploy/{deploy}")
	public ResponseEntity<ResponseDTO> deployProcess(@PathVariable long processId, @PathVariable boolean deploy) {
		return new ResponseEntity<>(service.deployProcess(processId, deploy), HttpStatus.OK);
	}

}
