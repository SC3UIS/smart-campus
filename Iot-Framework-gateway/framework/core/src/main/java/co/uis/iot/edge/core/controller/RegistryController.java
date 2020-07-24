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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.core.service.IRegistryService;

/**
 * Entry point for all registry related REST services.
 * 
 * @author Camilo Gutiérrez
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/registry")
public class RegistryController {

	private static final String REGISTRY_NOT_NULL = "Registry cannot be null";
	private static final String ID_NOT_NULL = "Registry id can't be null.";
	private static final String TYPE_NOT_NULL = "Registry type can't be null.";
	private static final String NAME_HAS_TEXT = "Registry name can't be null.";

	@Autowired
	private IRegistryService service;

	/**
	 * Finds all Registries by type and/or id (backend).
	 * 
	 * @param type of the Registries. Use <code>null</code> to find all.
	 * @param id   of the Registry to find, if specified also type is required.
	 * @return the list of Registries that match the given type and/or id.
	 */
	@GetMapping(value = "/registries", produces = "application/json")
	public ResponseEntity<List<RegistryDTO>> getRegistries(@RequestParam(required = false) final EThingType type,
			@RequestParam(required = false) final Long id) {
		if (id == null) {
			return new ResponseEntity<>(service.getRegistries(type), HttpStatus.OK);
		}
		Assert.notNull(type, "To find by id the device type should also be specified.");
		return new ResponseEntity<>(Collections.singletonList(service.getRegistriesByIdBackendAndType(type, id)),
				HttpStatus.OK);
	}

	/**
	 * Registers a device {GATEWAY, SENSOR or ACTUATOR}.
	 * 
	 * @param registryDTO the {@link Registry} to be created.
	 * @return the created Registry. For a Gateway it returns also the Reported
	 *         Properties.
	 */
	@PostMapping(value = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<RegistryDTO> createRegistry(@RequestBody final RegistryDTO registryDTO) {
		Assert.notNull(registryDTO, REGISTRY_NOT_NULL);
		Assert.notNull(registryDTO.getId(), ID_NOT_NULL);
		Assert.notNull(registryDTO.getType(), TYPE_NOT_NULL);
		Assert.hasText(registryDTO.getName(), NAME_HAS_TEXT);
		return new ResponseEntity<>(service.createRegistry(registryDTO), HttpStatus.OK);
	}

	/**
	 * Updates a registry and its properties.
	 * 
	 * @param registryDTO the {@link Registry} to be updated.
	 * @return the updated Registry. For a Gateway it returns also the Reported Properties.
	 */
	@PutMapping(value = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<RegistryDTO> updateRegistry(@RequestBody final RegistryDTO registryDTO) {
		Assert.notNull(registryDTO, REGISTRY_NOT_NULL);
		Assert.notNull(registryDTO.getId(), ID_NOT_NULL);
		Assert.notNull(registryDTO.getType(), TYPE_NOT_NULL);
		Assert.hasText(registryDTO.getName(), NAME_HAS_TEXT);
		return new ResponseEntity<>(service.updateRegistry(registryDTO), HttpStatus.OK);
	}

	/**
	 * Deletes a registry and its properties.
	 * 
	 * @param id   of the Registry to be deleted. Non <code>null</code>.
	 * @param type of the Registry to be deleted {@link EThingType}.
	 * @return the status code that determines whether the operation was successful
	 *         or not.
	 */
	@DeleteMapping(value = "")
	public ResponseEntity<HttpStatus> deleteRegistry(@RequestParam final Long id, @RequestParam final EThingType type) {
		Assert.notNull(id, ID_NOT_NULL);
		Assert.notNull(type, TYPE_NOT_NULL);
		service.deleteRegistry(id, type);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
