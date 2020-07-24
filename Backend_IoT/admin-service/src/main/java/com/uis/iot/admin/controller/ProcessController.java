package com.uis.iot.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import com.uis.iot.admin.entity.Process;
import com.uis.iot.admin.service.IProcessService;
import com.uis.iot.common.model.ProcessDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Entry point for all Process related REST services.
 * 
 * @author felipe.estupinan
 * @autor kevin.arias
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/processes")
public class ProcessController {

	@Autowired
	private IProcessService service;

	/**
	 * Saves a new process in the database.
	 * 
	 * @param processDTO the process object to be registered as a
	 *                   {@link ProcessDTO}.
	 * @return a {@link ProcessDTO} that contains the process DTO, but with an
	 *         assigned id.
	 */
	@PostMapping(path = "/process", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ProcessDTO> create(@RequestBody ProcessDTO processDTO) {
		Assert.hasText(processDTO.getName(), "Nombre requerido.");
		Assert.hasText(processDTO.getDescription(), "Descripción requerida.");
		Assert.isTrue(processDTO.getGatewayId() != 0, "El proceso debe estar asociado al menos a un gateway.");
		return new ResponseEntity<>(service.register(Process.fromDTO(processDTO)).toDTO(), HttpStatus.CREATED);
	}

	/**
	 * Updates the information of a process in the database.
	 * 
	 * @param processDTO of object of the process who will be registered
	 *                   {@link ProcessDTO}.
	 * @param processId  id of the process to be updated.
	 * @return a {@link ProcessDTO} that contains the process as DTO with the
	 *         updated information.
	 */
	@PutMapping(path = "/process/{processId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ProcessDTO> edit(@RequestBody ProcessDTO processDTO, @PathVariable long processId) {
		Assert.hasText(processDTO.getName(), "Nombre requerido.");
		Assert.hasText(processDTO.getDescription(), "Descripción requerida.");
		processDTO.setId(processId);
		return new ResponseEntity<>(service.edit(Process.fromDTO(processDTO)).toDTO(), HttpStatus.OK);
	}

	/**
	 * Deletes a process from the database.
	 * 
	 * @param processId id of the process to be deleted.
	 * @return a {@link ResponseEntity} that contains a message indicating whether
	 *         the process was deleted ot not.
	 */
	@DeleteMapping(value = "/process/{processId}", produces = "application/json")
	public ResponseEntity<ResponseDTO> delete(@PathVariable long processId) {
		return new ResponseEntity<>(service.delete(processId), HttpStatus.OK);
	}

	/**
	 * Gets a process from the database.
	 * 
	 * @param processId id of the process to be obtained
	 * @return a {@link ResponseEntity} that contains the DTO of the process
	 */
	@GetMapping(path = "/process/{processId}")
	public ResponseEntity<ProcessDTO> get(@PathVariable long processId) {
		return new ResponseEntity<>(service.getProcess(processId).toDTO(), HttpStatus.OK);
	}

	/**
	 * Gets all the Processes of the given Gateway.
	 * 
	 * @param gatewayId id of the Gateway.
	 * @return a {@link ResponseEntity} that contains a {@link List} of Processes as
	 *         DTOs, never <code>null</code>.
	 */
	@GetMapping(path = "/gateway/{gatewayId}")
	public ResponseEntity<List<ProcessDTO>> getByGateway(@PathVariable long gatewayId) {
		return new ResponseEntity<>(
				service.getProcessesByGateway(gatewayId).stream().map(Process::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Gets all the Processes of the given Application.
	 * 
	 * @param applicationId id of the Application.
	 * @return a {@link ResponseEntity} that contains a {@link List} of Processes as
	 *         DTOs, never <code>null</code>.
	 */
	@GetMapping(path = "/application/{applicationId}")
	public ResponseEntity<List<ProcessDTO>> getByApplication(@PathVariable long applicationId) {
		return new ResponseEntity<>(service.getProcessesByApplication(applicationId).stream().map(Process::toDTO)
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	/**
	 * Retrieves the processes for a given User.
	 * 
	 * @param userId id of the user.
	 * @return a {@link ResponseEntity} that contains the processes that belong to
	 *         the given user.
	 */
	@GetMapping(path = "/user/{userId}", produces = "application/json")
	public ResponseEntity<List<ProcessDTO>> getByUserId(@PathVariable final long userId) {
		return new ResponseEntity<>(
				service.getProcessesByUser(userId).stream().map(Process::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Retrieves the applications for a given topic.
	 * 
	 * @param topic topic to be found in the processes properties.
	 * @return a {@link ResponseEntity} that contains the processes that has a given
	 *         topic.
	 */
	@GetMapping(path = "/topic", produces = "application/json")
	public ResponseEntity<List<ProcessDTO>> getByTopic(@RequestParam("topic") String topic) {
		return new ResponseEntity<>(
				service.getProcessesByTopic(topic).stream().map(Process::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Returns the processes that match the param sent.
	 * 
	 * @param userId        id of the user which processes will be returned.
	 * @param applicationId id of the application which processes will be returned.
	 * @param topic         topic of the processes which will be returned.
	 * @param gatewayId     id of the gateway which processes will be returned.
	 * @param processId     id of the process which will be returned.
	 * @return a list of the process ids.
	 */
	@GetMapping(path = "/process/ids", produces = "application/json")
	public ResponseEntity<List<Long>> getProcessIds(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "applicationId", required = false) Long applicationId,
			@RequestParam(value = "topic", required = false) String topic,
			@RequestParam(value = "gatewayId", required = false) Long gatewayId,
			@RequestParam(value = "processId", required = false) Long processId) {

		Assert.isTrue(
				userId != null || applicationId != null || topic != null || gatewayId != null || processId != null,
				"Alguno de los parametros de búsqueda debe ser enviado");
		return new ResponseEntity<>(service.getProcessIds(userId, applicationId, topic, gatewayId, processId),
				HttpStatus.OK);
	}

	/**
	 * Deploys a given Process by its id. If the PROCESS_JAR property is not set
	 * then nothing is done.
	 * 
	 * @param processId the id of the Process.
	 * @param deploy    <code>true</code> to deploy/redeploy the process,
	 *                  <code>false</code> to undeploy it.
	 * @return a {@link ResponseDTO} that indicates if the operation succeeded or
	 *         not.
	 */
	@PutMapping(value = "/process/{processId}/deploy/{deploy}")
	public ResponseEntity<ResponseDTO> deployProcess(@PathVariable long processId, @PathVariable boolean deploy) {
		return new ResponseEntity<>(service.deployProcess(processId, deploy), HttpStatus.OK);
	}
}
