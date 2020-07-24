package com.uis.iot.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.openmbean.InvalidKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.uis.iot.admin.entity.Device;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.repository.IApplicationRepository;
import com.uis.iot.admin.repository.IDeviceRepository;
import com.uis.iot.admin.repository.IGatewayRepository;
import com.uis.iot.admin.utils.Utils;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.gateway.model.RegistryDTO;
import com.uis.iot.common.model.DeviceDTO;
import com.uis.iot.common.model.ResponseDTO;
import com.uis.iot.common.utils.CommonUtils;

/**
 * Logic to handle the management of Devices.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 */
@Service
public class DeviceService implements IDeviceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private IDeviceRepository repository;

	@Autowired
	private IGatewayService gatewayService;

	@Autowired
	private IGatewayRepository gatewayRepository;

	@Autowired
	private IApplicationRepository appRepository;

	@Override
	@Transactional
	public Device registerDevice(final Device device) {
		device.setId(null);
		final Gateway gateway = gatewayRepository.findById(device.getGateway().getId())
				.orElseThrow(() -> new InvalidKeyException("El gateway no existe"));
		try {
			final Device savedDevice = repository.save(device);
			final RegistryDTO deviceDTOafterGateway = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/registry",
					HttpMethod.POST, RegistryDTO.class, savedDevice.toDTOforGateway()).getBody();
			final Device deviceAfterGateway = Device.fromDTOfromGateway(deviceDTOafterGateway, gateway.getId());
			repository.save(deviceAfterGateway);
			return deviceAfterGateway;
		} catch (final ResourceAccessException e) {
			LOGGER.error("El dispositivo no pudo ser registrado: ", e);
			throw new InternalException("La solicitud para " + gateway.getIpGateway()
					+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.", e);
		} catch (final HttpStatusCodeException e) {
			LOGGER.error("El dispositivo no pudo ser registrado: ", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException(
					"El dispositivo no pudo ser registrado: " + CommonUtils.receiveGatewayExceptions(e));
		} catch (final Exception e) {
			LOGGER.error("El dispositivo no pudo ser registrado: ", e);
			throw new InternalException("El dispositivo no pudo ser registrado: ", e);
		}
	}

	@Override
	@Transactional
	public DeviceDTO editDevice(final DeviceDTO deviceDTO, final long idDevice) {
		final Device foundDevice = repository.findById(idDevice)
				.orElseThrow(() -> new InvalidKeyException("Dispositivo no encontrado."));
		final Gateway gateway = gatewayService.getGateway(deviceDTO.getGatewayId());
		final Device device = Device.fromDTO(deviceDTO);
		device.setId(idDevice);
		try {
			Gateway currentGateway = gatewayService.getGateway(foundDevice.getGateway().getId());
			ResponseEntity<RegistryDTO> response = null;
			if (currentGateway.getId() == gateway.getId()) {
				response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/registry", HttpMethod.PUT,
						RegistryDTO.class, device.toDTOforGateway());
			} else {
				response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/registry", HttpMethod.POST,
						RegistryDTO.class, device.toDTOforGateway());
				CommonUtils.getHTTPResponse(currentGateway.getIpGateway() + "/registry?id=" + device.getId() + "&type="
						+ device.getDeviceType(), HttpMethod.DELETE, HttpStatus.class);
			}
			final RegistryDTO deviceDTOAfterGateway = response.getBody();
			final Device deviceAfterGateway = Device.fromDTOfromGateway(deviceDTOAfterGateway,
					deviceDTO.getGatewayId());
			repository.save(deviceAfterGateway);
			return deviceAfterGateway.toDTO();
		} catch (final ResourceAccessException e) {
			LOGGER.error("El dispositivo no pudo ser editado: ", e);
			throw new InternalException("La solicitud para " + gateway.getIpGateway()
					+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.", e);
		} catch (final HttpStatusCodeException e) {
			LOGGER.error("El dispositivo no pudo ser editado: ", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException(
					"El dispositivo no pudo ser editado: " + CommonUtils.receiveGatewayExceptions(e));
		} catch (final Exception e) {
			LOGGER.error("El dispositivo no pudo ser editado: ", e);
			throw new InternalException("El dispositivo no pudo ser editado: ", e);
		}
	}

	@Override
	@Transactional
	public ResponseDTO deleteDevice(final long idDevice) {
		final Optional<Device> findById = repository.findById(idDevice);
		if (findById.isPresent()) {
			final Device device = findById.get();
			final Gateway gateway = gatewayRepository.findById(device.getGateway().getId())
					.orElseThrow(() -> new InvalidKeyException("El gateway no existe"));
			try {
				CommonUtils.getHTTPResponse(
						gateway.getIpGateway() + "/registry?id=" + device.getId() + "&type=" + device.getDeviceType(),
						HttpMethod.DELETE, HttpStatus.class);

				repository.deleteById(idDevice);
				return new ResponseDTO("Se ha eliminado el dispositivo correctamente.", true);

			} catch (final ResourceAccessException e) {
				LOGGER.error("El dispositivo no pudo ser eliminado: ", e);
				throw new InternalException("La solicitud para " + gateway.getIpGateway()
						+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
						e);
			} catch (final HttpStatusCodeException e) {
				LOGGER.error("El dispositivo no pudo ser eliminado: ", CommonUtils.receiveGatewayExceptions(e));
				throw new InternalException(
						"El dispositivo no pudo ser eliminado: " + CommonUtils.receiveGatewayExceptions(e));
			} catch (final Exception e) {
				LOGGER.error("El dispositivo no pudo ser eliminado: ", e);
				throw new InternalException("El dispositivo no pudo ser eliminado: ", e);
			}
		} else {
			LOGGER.error("Dispositivo no encontrado.");
			throw new InvalidKeyException("Dispositivo no encontrado.");
		}
	}

	@Override
	public DeviceDTO getDevice(final long idDevice) {
		final Optional<Device> findById = repository.findById(idDevice);
		if (findById.isPresent()) {
			final Device device = findById.get();
			return device.toDTO();
		} else {
			LOGGER.error("Dispositivo no encontrado.");
			throw new InvalidKeyException("Dispositivo no encontrado.");
		}
	}

	@Override
	public List<Device> getDevicesByGateway(final long gatewayId) {
		gatewayRepository.findById(gatewayId).orElseThrow(() -> new InvalidKeyException("El Gateway no existe"));
		final List<Device> devices = repository.findByGatewayId(gatewayId, Utils.NAME_ASC_SORT);
		devices.forEach(d -> d.setDeviceProperties(null));
		return devices;
	}

	@Override
	public List<Device> getDevicesByApplication(final long idApplication) {
		appRepository.findById(idApplication).orElseThrow(() -> new InvalidKeyException("La aplicación no existe"));
		final List<Gateway> appGateways = gatewayRepository.findByApplicationsId(idApplication, Utils.NAME_ASC_SORT);
		final List<Device> devices = new ArrayList<Device>();
		appGateways.forEach(g -> devices.addAll(repository.findByGatewayId(g.getId(), Utils.NAME_ASC_SORT)));
		devices.forEach(d -> d.setDeviceProperties(null));
		return devices;
	}

	@Override
	public List<Device> getDevicesByUser(final long userId) {
		List<Device> devices = repository.findByGatewayUserId(userId, Utils.NAME_ASC_SORT);
		return devices;
	}
}
