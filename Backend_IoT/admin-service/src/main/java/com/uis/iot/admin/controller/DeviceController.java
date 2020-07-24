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

import com.uis.iot.admin.entity.Device;
import com.uis.iot.admin.service.IDeviceService;
import com.uis.iot.common.model.DeviceDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Entry point for all Device related REST services.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/devices")
public class DeviceController {

	@Autowired
	private IDeviceService service;

	/**
	 * Registers a new device
	 * 
	 * @param deviceDTO the device object to be registered
	 * @return a {@link ResponseEntity} with the registered device dto
	 */
	@PostMapping(path = "/device", consumes = "application/json", produces = "application/json")
	public ResponseEntity<DeviceDTO> register(@RequestBody final DeviceDTO deviceDTO) {
		Assert.hasText(deviceDTO.getName(), "Nombre requerido.");
		Assert.hasText(deviceDTO.getDescription(), "Descripción requerida.");
		Assert.isTrue(deviceDTO.getGatewayId() != 0, "Gateway requerido.");
		Assert.isTrue(deviceDTO.getType() != null, "Tipo de dispositivo requerido.");

		return new ResponseEntity<>(service.registerDevice(Device.fromDTO(deviceDTO)).toDTO(), HttpStatus.CREATED);
	}

	/**
	 * Updates the information of a device in the database.
	 * 
	 * @param deviceDTO dto of the device who will be registered
	 * @param idDevice  id of the device to be updated
	 * @return a {@link ResponseEntity} with the device dto with the updated information
	 */
	@PutMapping(path = "/device/{idDevice}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<DeviceDTO> edit(@RequestBody final DeviceDTO deviceDTO, @PathVariable final long idDevice) {
		Assert.hasText(deviceDTO.getName(), "Nombre requerido.");
		Assert.hasText(deviceDTO.getDescription(), "Descripción requerida.");
		Assert.isTrue(deviceDTO.getGatewayId() != 0, "Gateway requerido.");
		Assert.isTrue(deviceDTO.getType() != null, "Tipo de dispositivo requerido.");

		return new ResponseEntity<>(service.editDevice(deviceDTO, idDevice), HttpStatus.OK);
	}

	/**
	 * Deletes a device from the database.
	 * 
	 * @param idDevice id of the device to be deleted
	 * @return a {@link ResponseEntity} with the response message indicating whether the device was deleted
	 */
	@DeleteMapping(value = "/device/{idDevice}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable final long idDevice) {
		return new ResponseEntity<>(service.deleteDevice(idDevice), HttpStatus.OK);
	}

	/**
	 * Gets a device from the database.
	 * 
	 * @param idDevice id of the device to be obtained
	 * @return {@link ResponseEntity} that contains the dto of the device
	 */
	@GetMapping(path = "/device/{idDevice}")
	public ResponseEntity<DeviceDTO> get(@PathVariable final long idDevice) {
		return new ResponseEntity<>(service.getDevice(idDevice), HttpStatus.OK);
	}

	/**
	 * Retrieves the devices for a given Gateway
	 * 
	 * @param idGateway id of the gateway.
	 * @return a {@link ResponseEntity} that contains the devices that belong to the
	 *         given gateway.
	 */
	@GetMapping("/gateway/{idGateway}")
	public ResponseEntity<List<DeviceDTO>> getDevicesByGateway(@PathVariable final long idGateway) {
		return new ResponseEntity<>(
				service.getDevicesByGateway(idGateway).stream().map(Device::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Retrieves the devices for a given Application
	 * 
	 * @param idApplication id of the app.
	 * @return a {@link ResponseEntity} that contains the devices that belong to the
	 *         given application.
	 */
	@GetMapping("/application/{idApplication}")
	public ResponseEntity<List<DeviceDTO>> getDevicesByApplication(@PathVariable final long idApplication) {
		return new ResponseEntity<>(
				service.getDevicesByApplication(idApplication).stream().map(Device::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Retrieves the devices for a given User
	 * 
	 * @param idUser id of the user.
	 * @return a {@link ResponseEntity} that contains the devices that belong to the
	 *         given user.
	 */
	@GetMapping("/user/{idUser}")
	public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable final long idUser) {
		return new ResponseEntity<>(
				service.getDevicesByUser(idUser).stream().map(Device::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}
}
