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
import org.springframework.web.bind.annotation.RestController;

import com.uis.iot.admin.entity.Application;
import com.uis.iot.admin.service.IApplicationService;
import com.uis.iot.common.model.ApplicationDTO;
import com.uis.iot.common.model.GatewayAssignmentDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Entry point for all Application related REST services.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/applications")
public class ApplicationController {

	@Autowired
	private IApplicationService service;

	private static final String NAME_REQUIRED = "Nombre requerido.";

	private static final String USER_ID_REQUIRED = "ID de usuario requerido.";

	private static final String DESCRIPTION_REQUIRED = "Descripción requerida.";

	/**
	 * Registers a new application in the database.
	 * 
	 * @param appDTO the application object to be registered as a {@link ApplicationDTO}.
	 * @return a {@link ApplicationDTO} that contains the application created.
	 */
	@PostMapping(path = "/application", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ApplicationDTO> register(@RequestBody final ApplicationDTO appDTO) {
		Assert.hasText(appDTO.getName(), NAME_REQUIRED);
		Assert.notNull(appDTO.getUserId(), USER_ID_REQUIRED);
		Assert.hasText(appDTO.getDescription(), DESCRIPTION_REQUIRED);

		return new ResponseEntity<>(service.registerApplication(Application.fromDTO(appDTO)).toDTO(),
				HttpStatus.CREATED);
	}

	/**
	 * Updates an existing application
	 * 
	 * @param appDTO the application object to be updated as a {@link ApplicationDTO}.
	 * @param idApp the application id
	 * @return a {@link ApplicationDTO} that contains the application updated.
	 */
	@PutMapping(path = "/application/{idApp}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ApplicationDTO> edit(@RequestBody final ApplicationDTO appDTO,
			@PathVariable final long idApp) {
		Assert.hasText(appDTO.getName(), NAME_REQUIRED);
		Assert.notNull(appDTO.getUserId(), USER_ID_REQUIRED);
		Assert.hasText(appDTO.getDescription(), DESCRIPTION_REQUIRED);
		appDTO.setId(idApp);

		return new ResponseEntity<>(service.editApplication(Application.fromDTO(appDTO)).toDTO(), HttpStatus.OK);
	}

	/**
	 * Retrieves the applications for a given User sorted by name.
	 * 
	 * @param userId - id of the user.
	 * @return a list of {@link ApplicationDTO} that contains the applications that belong to the given user.
	 */
	@GetMapping(path = "/user/{userId}", produces = "application/json")
	public ResponseEntity<List<ApplicationDTO>> getApplicationsByUserId(@PathVariable final long userId) {
		return new ResponseEntity<>(
				service.getApplicationsByUserId(userId).stream().map(Application::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Retrieves an application by it's id.
	 * 
	 * @param appId id of the application.
	 * @return a {@link ApplicationDTO} that contains the application with the given id.
	 */
	@GetMapping(path = "/application/{idApp}", produces = "application/json")
	public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable final long idApp) {
		return new ResponseEntity<>(service.getApplicationById(idApp).toDTO(), HttpStatus.OK);
	}

	/**
	 * Assigns or unassigns a Gateway to an application.
	 * 
	 * @param assignment {@link GatewayAssignmentDTO} that indicates which gateway is being assigned to which application.
	 * @param assign <code>true</code> to assign the Gateway, <code>false</code>to unassign it.
	 * @return a {@link ResponseDTO} that indicates if the assignment was sucessfully or not.
	 */
	@PutMapping(path = "/application/gateway/{assign}", produces = "application/json")
	public ResponseEntity<ResponseDTO> assignGatewayToApplication(@RequestBody final GatewayAssignmentDTO assignment,
			@PathVariable final boolean assign) {
		service.assignGatewayToApplication(assignment.getApplicationId(), assignment.getGatewayId(), assign);
		return new ResponseEntity<>(
				new ResponseDTO((assign ? "Asignación" : "Desasignación") + " realizada correctamente.", true),
				HttpStatus.OK);
	}

	/**
	 * Deletes an application from the database.
	 * 
	 * @param idApp id of the application to be deleted
	 * @return response message indicating whether the application was deleted
	 */
	@DeleteMapping(value = "/application/{idApp}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable final long idApp) {
		return new ResponseEntity<>(service.deleteApplication(idApp), HttpStatus.OK);
	}
}
