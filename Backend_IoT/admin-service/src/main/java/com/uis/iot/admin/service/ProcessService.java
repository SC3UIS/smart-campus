package com.uis.iot.admin.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.entity.Process;
import com.uis.iot.admin.entity.ProcessProperty;
import com.uis.iot.admin.repository.IGatewayRepository;
import com.uis.iot.admin.repository.IProcessRepository;
import com.uis.iot.admin.utils.Utils;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.gateway.model.ProcessDTO;
import com.uis.iot.common.model.ResponseDTO;
import com.uis.iot.common.utils.CommonUtils;

/**
 * Logic to handle the management of Processes.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 */
@Service
public class ProcessService implements IProcessService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

	private static final String PROCESS_NOT_FOUND = "El proceso no existe.";
	private static final String TOPIC_PROP_NAME = "topic";

	@Autowired
	private IProcessRepository repository;

	@Autowired
	private IGatewayRepository gatewayRepository;

	@Autowired
	private IGatewayService gatewayService;

	@Autowired
	private IUserService userService;

	@Override
	@Transactional(readOnly = true)
	public Process getProcess(long processId) {
		return repository.findById(processId).orElseThrow(() -> new InvalidKeyException(PROCESS_NOT_FOUND));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Process> getProcessesByGateway(long gatewayId) {
		return repository.findByGatewayId(gatewayId, Utils.NAME_ASC_SORT);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Process> getProcessesByApplication(long applicationId) {
		return repository.findByGatewayApplicationsId(applicationId, Utils.NAME_ASC_SORT);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Process> getProcessesByUser(long userId) {
		final AppUser user = userService.getUser(userId);
		return user.isAdmin() ? (List<Process>) repository.findAll(Utils.NAME_ASC_SORT)
				: repository.findByGatewayUserId(userId, Utils.NAME_ASC_SORT);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Process> getProcessesByTopic(String topic) {
		return repository.findByProcessPropertiesIdNameAndProcessPropertiesValue(TOPIC_PROP_NAME, topic);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> getProcessIds(Long userId, Long applicationId, String topic, Long gatewayId, Long processId) {
		List<Process> processes = null;
		if (userId != null) {
			processes = getProcessesByUser(userId);
		} else if (applicationId != null) {
			processes = getProcessesByApplication(applicationId);
		} else if (topic != null) {
			processes = getProcessesByTopic(topic);
		} else if (gatewayId != null) {
			processes = getProcessesByGateway(gatewayId);
		} else {
			return Collections.singletonList(processId);
		}

		return processes.stream().map(Process::getId).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Process register(Process process) {
		process.setId(null);
		final Gateway gateway = gatewayRepository.findById(process.getGateway().getId())
				.orElseThrow(() -> new InvalidKeyException("El gateway no existe"));
		Assert.isTrue(
				process.getProcessProperties().stream()
						.anyMatch(property -> property.getId().getName().equalsIgnoreCase(TOPIC_PROP_NAME)),
				"El proceso no contiene la propiedad obligatoria TOPIC");
		try {
			process.getProcessProperties().forEach(prop -> prop.setProcess(process));
			final Process savedProcess = repository.save(process);
			ResponseEntity<ProcessDTO> response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/process",
					HttpMethod.POST, ProcessDTO.class, savedProcess.toDTOforGateway());
			return Process.fromDTOfromGateway(response.getBody(), savedProcess.getGatewayId());
		} catch (ResourceAccessException e) {
			LOGGER.error("El proceso no pudo ser registrado.", e);
			throw new InternalException(
					"La solicitud para registrar el proceso en el gateway no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
					e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("El proceso no pudo ser registrado: {}", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException("El proceso no pudo ser registrado: " + CommonUtils.receiveGatewayExceptions(e),
					e);
		} catch (Exception e) {
			LOGGER.error("El proceso no pudo ser registrado.", e);
			throw new InternalException("El proceso no pudo ser registrado.", e);
		}
	}

	@Override
	@Transactional
	public Process edit(Process process) {
		final List<ProcessProperty> properties = process.getProcessProperties();
		Assert.isTrue(
				properties.stream().anyMatch(property -> property.getId().getName().equalsIgnoreCase(TOPIC_PROP_NAME)),
				"El proceso no contiene la propiedad obligatoria TOPIC");
		try {
			Process currentProcess = getProcess(process.getId());
			Gateway currentGateway = gatewayService.getGateway(currentProcess.getGatewayId());
			final Gateway gateway = gatewayService.getGateway(process.getGatewayId());

			ResponseEntity<ProcessDTO> response = null;
			properties.forEach(prop -> prop.setProcess(process));
			Process savedProcess = repository.save(process);
			if (currentGateway.getId().equals(gateway.getId())) {
				response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/process", HttpMethod.PUT,
						ProcessDTO.class, savedProcess.toDTOforGateway());
			} else {
				response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/process", HttpMethod.POST,
						ProcessDTO.class, savedProcess.toDTOforGateway());
				CommonUtils.getHTTPResponse(currentGateway.getIpGateway() + "/process/" + process.getId(),
						HttpMethod.DELETE, HttpStatus.class);
			}
			return Process.fromDTOfromGateway(response.getBody(), savedProcess.getGatewayId());
		} catch (ResourceAccessException e) {
			LOGGER.error("El proceso no pudo ser editado.", e);
			throw new InternalException(
					"La solicitud para editar el proceso en el gateway no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
					e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("El proceso no pudo ser editado. {}", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException("El proceso no pudo ser editado." + CommonUtils.receiveGatewayExceptions(e), e);
		} catch (Exception e) {
			LOGGER.error("El proceso no pudo ser editado.", e);
			throw new InternalException("El proceso no pudo ser editado.", e);
		}
	}

	@Override
	@Transactional
	public ResponseDTO delete(long processId) {
		try {
			final Gateway gateway = getProcess(processId).getGateway();
			repository.deleteById(processId);
			CommonUtils.getHTTPResponse(gateway.getIpGateway() + "/process/" + processId, HttpMethod.DELETE,
					HttpStatus.class);
			return new ResponseDTO("Se ha eliminado el proceso correctamente.", true);
		} catch (ResourceAccessException e) {
			LOGGER.error("El proceso no pudo ser eliminado.", e);
			throw new InternalException(
					"La solicitud para eliminar el proceso en el gateway no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
					e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("El proceso no pudo ser eliminado. {}", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException("El proceso no pudo ser eliminado: " + CommonUtils.receiveGatewayExceptions(e),
					e);
		} catch (Exception e) {
			LOGGER.error("El proceso no pudo ser eliminado.", e);
			throw new InternalException("El proceso no pudo ser eliminado.", e);
		}
	}

	@Override
	@Transactional
	public ResponseDTO deployProcess(long processId, boolean deploy) {
		try {
			final Process process = getProcess(processId);
			final Gateway gateway = process.getGateway();
			repository.updateProcessAliveStatus(processId, deploy);
			final ResponseEntity<ResponseDTO> response = CommonUtils.getHTTPResponse(
					String.format("%s/process/%d/deploy/%s", gateway.getIpGateway(), processId, deploy), HttpMethod.PUT,
					ResponseDTO.class);
			return response.getBody();
		} catch (ResourceAccessException e) {
			LOGGER.error("El proceso no pudo ser desplegado.", e);
			throw new InternalException(
					"La solicitud para desplegar el proceso en el gateway no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
					e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("El proceso no pudo ser desplegado. {}", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException("El proceso no pudo ser desplegado: " + CommonUtils.receiveGatewayExceptions(e),
					e);
		} catch (Exception e) {
			LOGGER.error("El proceso no pudo ser desplegado.", e);
			throw new InternalException("El proceso no pudo ser desplegado.", e);
		}
	}

}
