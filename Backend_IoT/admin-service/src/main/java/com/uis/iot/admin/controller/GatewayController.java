package com.uis.iot.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.entity.Notification;
import com.uis.iot.admin.entity.Process;
import com.uis.iot.admin.service.IGatewayService;
import com.uis.iot.common.model.AppUserDTO;
import com.uis.iot.common.model.GatewayAssignmentDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.GatewayIpDTO;
import com.uis.iot.common.model.ResponseDTO;
import com.uis.iot.common.model.TopicDTO;
import com.uis.iot.common.model.UserNotificationsDTO;

/**
 * Entry point for all Gateway related REST services.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/gateways")
public class GatewayController {

	@Autowired
	private IGatewayService service;

	/**
	 * Registers a new gateway
	 * 
	 * @param gatewayDTO the gateway object to be registered
	 * @return
	 */
	@PostMapping(path = "/gateway", consumes = "application/json", produces = "application/json")
	public ResponseEntity<GatewayDTO> register(@RequestBody final GatewayDTO gatewayDTO) {
		Assert.hasText(gatewayDTO.getIp(), "Dirección Ip requerida.");
		Assert.hasText(gatewayDTO.getName(), "Nombre requerido.");
		Assert.hasText(gatewayDTO.getDescription(), "Descripcion requerido.");
		Assert.notNull(gatewayDTO.getUserId(), "Id de usuario requerido.");
		Assert.notNull(gatewayDTO.isAlive(), "Campo \"alive\" requerido.");

		return new ResponseEntity<>(service.registerGateway(gatewayDTO), HttpStatus.CREATED);
	}

	/**
	 * Edits a gateway
	 * 
	 * @param gatewayDTO the gateway object to be edited
	 * @param gatewayId the id of the gateway to be edited
	 * @return
	 */
	@PutMapping(path = "/gateway/{gatewayId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<GatewayDTO> edit(@RequestBody final GatewayDTO gatewayDTO, @PathVariable final long gatewayId) {
		Assert.hasText(gatewayDTO.getIp(), "Dirección Ip requerida.");
		Assert.hasText(gatewayDTO.getName(), "Nombre requerido.");
		Assert.hasText(gatewayDTO.getDescription(), "Descripcion requerido.");
		Assert.notNull(gatewayDTO.getUserId(), "Id de usuario requerido.");
		Assert.notNull(gatewayDTO.isAlive(), "Campo \"alive\" requerido.");

		return new ResponseEntity<>(service.editGateway(gatewayDTO, gatewayId), HttpStatus.OK);
	}

	/**
	 * Gets a gateway from the database.
	 * 
	 * @param idGateway id of the gateway to be obtained
	 * @return dto of the gateway
	 */
	@GetMapping(path = "/gateway/{idGateway}", produces = "application/json")
	public ResponseEntity<GatewayDTO> get(@PathVariable final long idGateway) {
		return new ResponseEntity<>(service.getGateway(idGateway).toDTO(), HttpStatus.OK);
	}

	/**
	 * Gets all gateways from the database with their corresponding processes.
	 * 
	 * @param processes indicates whether the gateway will return its processes or not.
	 * @return list of gateway dtos
	 */
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<GatewayDTO>> getAll(@RequestParam(value = "processes", defaultValue = "false") final boolean processes) {
		final List<Gateway> gateways = service.getAllGateways(processes);
			return new ResponseEntity<>(
				gateways.stream().map(gateway -> {
					final GatewayDTO dto = new GatewayDTO(gateway.getId(), gateway.getIpGateway(), gateway.getDescription(), gateway.getName(), gateway.isAlive());
					if (processes) {
						dto.setProcesses(gateway.getProcesses().stream()
								.map(Process::toDTO).collect(Collectors.toList()));
					}
					return dto;
				})
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	/**
	 * Gets gateways from the database that belong to the Application with id
	 * idApplication.
	 * 
	 * @return list of gateway dtos
	 */
	@GetMapping(path = "/application/{idApplication}")
	public ResponseEntity<List<GatewayDTO>> getGatewaysByApplicationId(@PathVariable final long idApplication) {
		return new ResponseEntity<>(service.getGatewaysByApplicationId(idApplication).stream().map(Gateway::toDTO)
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	/**
	 * Gets gateways from the database that have a Topic property whose value is
	 * topic.
	 * 
	 * @return list of gateway dtos
	 */
	@GetMapping(path = "/topic")
	public ResponseEntity<List<GatewayDTO>> getGatewaysByTopic(@RequestParam final String topic) {
		return new ResponseEntity<>(
				service.getGatewaysByTopic(topic).stream().map(Gateway::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Gets gateways from the database that have a Topic property whose value is
	 * topic.
	 * 
	 * @return gatewaydto
	 */
	@GetMapping(path = "/process/{process}")
	public ResponseEntity<GatewayDTO> getGatewaysByProcess(@PathVariable final Long process) {
		return new ResponseEntity<>(service.getGatewayByProcess(process).toDTO(), HttpStatus.OK);
	}

	/**
	 * Deletes a gateway from the database.
	 * 
	 * @param idGateway id of the gateway to be deleted
	 * @return response message indicating whether the gateway was deleted
	 */
	@DeleteMapping(value = "/gateway/{gatewayId}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable final long gatewayId) {
		return new ResponseEntity<>(service.deleteGateway(gatewayId), HttpStatus.OK);
	}

	/**
	 * Gets the IPs of all the gateways from the database that have a Topic property
	 * whose value is topic.
	 * 
	 * @return list of gateway dtos
	 */
	@GetMapping(path = "/ip/topic")
	public ResponseEntity<List<GatewayIpDTO>> getGatewayIpsByTopic(@RequestParam final String topic) {
		return new ResponseEntity<>(
				service.getGatewaysByTopic(topic).stream().map(Gateway::toIpDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Gets the IPs of gateways from the database that have a certain process
	 * identified by processId.
	 * 
	 * @return list of gateway dtos
	 */
	@PutMapping(path = "/ip/process", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<GatewayIpDTO>> getGatewayIpsByProcesses(@RequestBody final List<Long> processIds) {
		return new ResponseEntity<>(
				service.getGatewaysByProcesses(processIds).stream().map(Gateway::toIpDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Gets a list of the IPs of the gateways by topic.
	 * 
	 * @param idGateway id of the gateway
	 * @return list of pairs <idProcess, topic>
	 */
	@GetMapping(path = "/gateway/{gatewayId}/topics")
	public ResponseEntity<List<TopicDTO>> getTopics(@PathVariable final long gatewayId) {
		return new ResponseEntity<>(service.getAssociatedTopics(gatewayId), HttpStatus.OK);
	}

	/**
	 * Assigns a gateway to an App.
	 * 
	 * @param idGateway id of the gateway to be deleted
	 * @return response message indicating whether the gateway was deleted
	 */
	@PutMapping(value = "/application/gateway/assignment")
	public ResponseEntity<ResponseDTO> assignToApp(@RequestBody final GatewayAssignmentDTO assignmentDTO) {
		return new ResponseEntity<>(service.assignToApp(assignmentDTO), HttpStatus.OK);
	}

	/**
	 * Sends the keep alive request to a gateway identified by its id and then
	 * creates, persists and returns all the notifications (grouped by user)
	 * required for the changes that occurred.
	 * 
	 * @param gatewayId id of the Gateway.
	 * @return a {@link ResponseEntity} with a list of {@link UserNotificationsDTO},
	 *         never <code>null</code>
	 */
	@GetMapping(value = "/gateway/{gatewayId}/keepAlive")
	public ResponseEntity<List<UserNotificationsDTO>> sendKeepAlive(@PathVariable final long gatewayId) {
		final List<UserNotificationsDTO> usersNotifications = new ArrayList<>();
		AppUserDTO user;
		UserNotificationsDTO userNotifications;
		for (final Entry<AppUser, List<Notification>> notificationsByUserEntry : service.sendKeepAlive(gatewayId)
				.entrySet()) {
			user = notificationsByUserEntry.getKey().toDTO();
			userNotifications = new UserNotificationsDTO(user,
					notificationsByUserEntry.getValue().stream().map(Notification::toDTO).collect(Collectors.toList()));
			usersNotifications.add(userNotifications);
		}
		return new ResponseEntity<>(usersNotifications, HttpStatus.OK);

	}

	/**
	 * Retrieves the gateways for a given User sorted by name.
	 * 
	 * @param userId - id of the user.
	 * @return a {@link ResponseEntity} that contains the gateways that belong to
	 *         the given user.
	 */
	@GetMapping(path = "/user/{userId}", produces = "application/json")
	public ResponseEntity<List<GatewayDTO>> getGatewaysByUserId(@PathVariable final long userId) {
		return new ResponseEntity<>(
				service.getGatewaysByUserId(userId).stream().map(gateway -> gateway.toDTO(true)).collect(Collectors.toList()),
				HttpStatus.OK);
	}
}
