package com.uis.iot.admin.service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.entity.Application;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.entity.Notification;
import com.uis.iot.admin.entity.Process;
import com.uis.iot.admin.repository.IApplicationRepository;
import com.uis.iot.admin.repository.IGatewayRepository;
import com.uis.iot.admin.repository.INotificationRepository;
import com.uis.iot.admin.repository.IProcessRepository;
import com.uis.iot.admin.repository.IUserRepository;
import com.uis.iot.admin.utils.Utils;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.exception.RecordExistsException;
import com.uis.iot.common.gateway.model.EPropertyType;
import com.uis.iot.common.gateway.model.EThingType;
import com.uis.iot.common.gateway.model.ProcessAliveDTO;
import com.uis.iot.common.gateway.model.RegistryDTO;
import com.uis.iot.common.model.GatewayAssignmentDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.GatewayPropertyDTO;
import com.uis.iot.common.model.ResponseDTO;
import com.uis.iot.common.model.TopicDTO;
import com.uis.iot.common.utils.CommonUtils;
import com.uis.iot.common.utils.ERoute;

/**
 * Logic to handle the management of Gateways.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 */
@Service
public class GatewayService implements IGatewayService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);

	@Autowired
	private IGatewayRepository repository;

	@Autowired
	private IApplicationRepository appRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IProcessRepository processRepository;

	@Autowired
	private INotificationRepository notificationRepository;
	
	@Value("${broker.url}")
	private String url;

	@Override
	public Gateway getGateway(final long idGateway) {
		final Optional<Gateway> findById = repository.findById(idGateway);
		if (findById.isPresent()) {
			return findById.get();
		} else {
			LOGGER.error("Gateway no encontrado.");
			throw new InvalidKeyException("Gateway no encontrado.");
		}
	}

	@Override
	@Transactional
	public GatewayDTO registerGateway(final GatewayDTO gatewayDTO) {
		gatewayDTO.setId(null);
		final AppUser user = userRepository.findById(gatewayDTO.getUserId())
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe"));
		final List<GatewayPropertyDTO> properties = gatewayDTO.getProperties();
		properties.add(new GatewayPropertyDTO(EPropertyType.CONFIG, null, "broker_url", url));
		try {
			final Gateway gateway = Gateway.fromDTO(gatewayDTO);
			final Gateway savedGateway = repository.save(gateway);
			// Subscribe to the new topic (gatewayId)
			CommonUtils.getHTTPResponse(ERoute.DATA + "/data/subscribe/" + savedGateway.getId(), HttpMethod.GET,
					String.class);
			final ResponseEntity<RegistryDTO> response = CommonUtils.getHTTPResponse(gatewayDTO.getIp() + "/registry",
					HttpMethod.POST, RegistryDTO.class, savedGateway.toDTOforGateway());

			final RegistryDTO gatewayDTOafterGateway = response.getBody();
			final Gateway gatewayFromGateway = Gateway.fromDTOfromGateway(gatewayDTOafterGateway, gatewayDTO.getIp(),
					gateway.getUser());
			gatewayFromGateway.setUser(user);
			repository.save(gatewayFromGateway);
			return gatewayFromGateway.toDTO();

		} catch (final ResourceAccessException e) {
			LOGGER.error("El gateway no pudo ser registrado: ", e);
			throw new InternalException("La solicitud para " + gatewayDTO.getIp()
					+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.", e);
		} catch (final HttpStatusCodeException e) {
			LOGGER.error("El gateway no pudo ser registrado: ", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException(
					"El gateway no pudo ser registrado: " + CommonUtils.receiveGatewayExceptions(e));
		} catch (final Exception e) {
			LOGGER.error("El gateway no pudo ser registrado: ", e);
			throw new InternalException("El gateway no pudo ser registrado: ", e);
		}
	}

	@Override
	@Transactional
	public GatewayDTO editGateway(final GatewayDTO gatewayDTO, final long idGateway) {
		userRepository.findById(gatewayDTO.getUserId())
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe"));
		final List<GatewayPropertyDTO> properties = gatewayDTO.getProperties();
		properties.stream().filter(p -> p.getName().equals("broker_url")).findFirst()
				.orElseThrow(() -> new InvalidKeyException("El gateway debe tener la propiedad broker_url"))
				.setValue(url);

		final Optional<Gateway> findById = repository.findById(idGateway);
		Gateway gateway = null;
		if (findById.isPresent()) {
			gatewayDTO.setId(idGateway);
			gateway = Gateway.fromDTO(gatewayDTO);
		} else {
			LOGGER.error("Gateway no encontrado.");
			throw new InvalidKeyException("Gateway no encontrado.");
		}
		try {
			final Gateway currentGateway = this.getGateway(idGateway);
			final Gateway savedGateway = repository.save(gateway);

			if (!currentGateway.getIpGateway().equals(savedGateway.getIpGateway())) {
				try {
					CommonUtils.getHTTPResponse(currentGateway.getIpGateway() + "/registry?id=" + gateway.getId()
							+ "&type=" + EThingType.GATEWAY, HttpMethod.DELETE, HttpStatus.class);
				} catch (final Exception e) {
					LOGGER.error(
							"El gateway en la IP anterior no pudo ser eliminado porque ocurrió un error o porque no existía, solo se movió: ",
							e);
				}
			}
			final ResponseEntity<RegistryDTO> response = CommonUtils.getHTTPResponse(
					gateway.getIpGateway() + "/registry", HttpMethod.PUT, RegistryDTO.class,
					savedGateway.toDTOforGateway());

			final RegistryDTO gatewayDTOafterGateway = response.getBody();
			final Gateway fromDTOfromGateway = Gateway.fromDTOfromGateway(gatewayDTOafterGateway, gatewayDTO.getIp(),
					savedGateway.getUser());
			repository.save(fromDTOfromGateway);
			return fromDTOfromGateway.toDTO();

		} catch (final ResourceAccessException e) {
			LOGGER.error("El gateway no pudo ser modificado: ", e);
			throw new InternalException("La solicitud para " + gatewayDTO.getIp()
					+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.", e);
		} catch (final HttpStatusCodeException e) {
			LOGGER.error("El gateway no pudo ser modificado: ", CommonUtils.receiveGatewayExceptions(e));
			throw new InternalException(
					"El gateway no pudo ser modificado: " + CommonUtils.receiveGatewayExceptions(e));
		} catch (final Exception e) {
			LOGGER.error("El gateway no pudo ser modificado: ", e);
			throw new InternalException("El gateway no pudo ser modificado: ", e);
		}
	}

	@Override
	public ResponseDTO deleteGateway(final long idGateway) {
		final Optional<Gateway> findById = repository.findById(idGateway);
		if (findById.isPresent()) {
			final Gateway gateway = findById.get();
			try {
				CommonUtils.getHTTPResponse(
						gateway.getIpGateway() + "/registry?id=" + gateway.getId() + "&type=" + EThingType.GATEWAY,
						HttpMethod.DELETE, HttpStatus.class);
				repository.deleteById(idGateway);
				return new ResponseDTO("Se ha eliminado el gateway correctamente.", true);
			} catch (final ResourceAccessException e) {
				LOGGER.error("El gateway no pudo ser eliminado: ", e);
				throw new InternalException("La solicitud para " + gateway.getIpGateway()
						+ " no pudo ser procesada, la conexión expiró porque el destinatario no pudo ser alcanzado.",
						e);
			} catch (final HttpStatusCodeException e) {
				LOGGER.error("El gateway no pudo ser eliminado: ", CommonUtils.receiveGatewayExceptions(e));
				throw new InternalException(
						"El gateway no pudo ser eliminado: " + CommonUtils.receiveGatewayExceptions(e));
			} catch (final Exception e) {
				LOGGER.error("El gateway no pudo ser eliminado: ", e);
				throw new InternalException("El gateway no pudo ser eliminado: ", e);
			}
		} else {
			LOGGER.error("Gateway no encontrado.");
			throw new InvalidKeyException("Gateway no encontrado.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Gateway> getAllGateways(final boolean processes) {
		final List<Gateway> gateways = (List<Gateway>) repository.findAll();
		if (processes) {
			gateways.forEach(gateway -> gateway.getProcesses());
		}
		return gateways;
	}

	@Override
	public List<Gateway> getGatewaysByApplicationId(final long applicationId) {
		final Application application = appRepository.findById(applicationId)
				.orElseThrow(() -> new InvalidKeyException("La aplicación no existe"));

		final List<Gateway> gateways = repository.findByApplicationsId(application.getId(), Utils.NAME_ASC_SORT);

		gateways.forEach(g -> {
			g.getProcesses().clear();
			g.getDevices().clear();
			g.getGatewayProperties().clear();
		});

		return gateways;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Gateway> getGatewaysByTopic(final String topic) {
		final List<Gateway> gateways = (List<Gateway>) repository.findAll();

//		List<Gateway> filteredGateways = gateways.stream()
//				.filter(g -> g.getGatewayProperties().stream()
//						.anyMatch(p -> p.getId().getName().equals("Topic") && p.getValue().equals(topic)))
//				.collect(Collectors.toList());

		final List<Gateway> filteredGateways = gateways.stream()
				.filter(g -> g.getProcesses().stream()
						.anyMatch(p -> p.getProcessProperties().stream()
								.anyMatch(pr -> pr.getId().getName().equals("topic") && pr.getValue().equals(topic))))
				.collect(Collectors.toList());

		filteredGateways.forEach(g -> g.getGatewayProperties().clear());
		return filteredGateways;
	}

	@Override
	public ResponseDTO assignToApp(final GatewayAssignmentDTO assignmentDTO) {
		final Application application = appRepository.findById(assignmentDTO.getApplicationId())
				.orElseThrow(() -> new InvalidKeyException("La aplicación no existe"));
		final Gateway gateway = repository.findById(assignmentDTO.getGatewayId())
				.orElseThrow(() -> new InvalidKeyException("El Gateway no existe"));
		gateway.getApplications().add(application);
		try {
			repository.save(gateway);
		} catch (final Exception e) {
			LOGGER.error("Esta asignación ya existe.");
			throw new RecordExistsException("Esta asignación ya existe.");
		}
		return new ResponseDTO("Gateway asignado correctamente.", true);
	}

	@Override
	@Transactional
	public MultiValueMap<AppUser, Notification> sendKeepAlive(final long gatewayId) {
		final Gateway gateway = getGateway(gatewayId);
		String exceptionMessage = null;

		// null if no change occurred, true indicates that now is alive, false otherwise
		Boolean gatewayCurrentStatus = null;
		ResponseEntity<ProcessAliveDTO[]> response = null;

		final List<ProcessAliveDTO> processAliveChanges = new ArrayList<>();
		final Map<Long, String> processNamesMap = new HashMap<>();
		try {
			response = CommonUtils.getHTTPResponse(gateway.getIpGateway() + "communication/keepAlive", HttpMethod.GET,
					ProcessAliveDTO[].class);
		} catch (final RestClientException e) {
			LOGGER.error("Exception consuming Keep Alive REST Service for Gateway {}", gatewayId, e);
			exceptionMessage = e.getMessage();
		}

		final Instant keepAliveDate = Instant.now();
		final StringBuilder messageBuilder = new StringBuilder(256);
		try {
			messageBuilder.append("El gateway ").append(gateway.getName());
			if ((response == null || !(response.getBody() instanceof ProcessAliveDTO[])) && gateway.isAlive()) {
				// Gateway is not answering and the Gateway was alive previously.
				messageBuilder.append(" no responde.");
				if (!StringUtils.isEmpty(exceptionMessage)) {
					messageBuilder.append(" Razón: ").append(exceptionMessage);
				}
				gatewayCurrentStatus = false;
				// Mark processes as dead also (only the ones that were alive).
				for (final Process process : gateway.getProcesses()) {
					if (process.isAlive()) {
						processAliveChanges.add(new ProcessAliveDTO(process.getId(), false));
						processNamesMap.put(process.getId(), process.getName());
					}
				}
				LOGGER.info("Gateway {} and its processes are dead (gateway is not answering).", gatewayId);
			} else if (response != null && (response.getBody() instanceof ProcessAliveDTO[])) {
				if (!gateway.isAlive()) {
					// Gateway answered the request and was dead previously.
					gatewayCurrentStatus = true;
					messageBuilder.append(" se encuentra disponible nuevamente.");
				}
				// Start validating the Processes.
				final Map<Long, Boolean> processesPreviousStatus = new HashMap<>();
				for (final Process process : gateway.getProcesses()) {
					processesPreviousStatus.put(process.getId(), process.isAlive());
					processNamesMap.put(process.getId(), process.getName());
				}
				for (final ProcessAliveDTO processStatus : response.getBody()) {
					if (processStatus.getIsAlive() != processesPreviousStatus.get(processStatus.getId())) {
						// Status change for the Process.
						processAliveChanges.add(processStatus);
					}
				}
			}
			return createNotificationsForKeepAlive(gateway, gatewayCurrentStatus, processAliveChanges, processNamesMap,
					keepAliveDate, messageBuilder);
		} catch (final DataAccessException e) {
			LOGGER.error("An error occurred in keep alive process for Gateway {} ({})", gateway.getName(),
					gateway.getId(), e);
			return new LinkedMultiValueMap<>();
		}
	}

	/**
	 * Creates and inserts all the notifications required for the given Gateway for
	 * the current keep alive process.
	 * 
	 * @param gateway              processed currently.
	 * @param gatewayCurrentStatus a {@link Boolean} if <code>null</code> no changes
	 *                             occurred for the Gateway, <code>true</code>
	 *                             indicates that the Gateway is now alive,
	 *                             <code>false</code> that is dead.
	 * @param processAliveChanges  a {@link List} of {@link ProcessAliveDTO} changes
	 *                             for the Processes associated to the current
	 *                             gateway.
	 * @param processNamesMap      a {@link Map} of all Processes names mapped by
	 *                             their id used to write Notification's body.
	 * @param keepAliveDate        {@link Instant} where the Keep Alive process
	 *                             started.
	 * @param messageBuilder       {@link StringBuilder} that contains the header
	 *                             for the body of the Gateway Notification.
	 * @return a {@link MultiValueMap} of all the Notifications created and inserted
	 *         by User, never <code>null</code>.
	 * @throws DataAccessException if the insertions of the notifications failed.
	 */
	private MultiValueMap<AppUser, Notification> createNotificationsForKeepAlive(final Gateway gateway,
			final Boolean gatewayCurrentStatus, final List<ProcessAliveDTO> processAliveChanges,
			final Map<Long, String> processNamesMap, final Instant keepAliveDate, StringBuilder messageBuilder) {
		final long gatewayId = gateway.getId();
		// Create notifications (not including users).
		final List<Notification> baseNotifications = new ArrayList<>();
		final Set<AppUser> administrators = userRepository.findByAdminTrue();
		Notification notification;
		final Set<AppUser> gatewayOwners = userRepository.findByGatewaysId(gatewayId);
		gatewayOwners.addAll(administrators);
		if (gatewayCurrentStatus != null) {
			repository.updateGatewayAliveStatus(gatewayId, gatewayCurrentStatus);
			// Create Notification for Gateway change.
			notification = new Notification(null, gatewayCurrentStatus, false, false, messageBuilder.toString(),
					Date.from(keepAliveDate));
			notification.setGateway(gateway);
			baseNotifications.add(notification);
		}
		// Create Notifications for Processes changes.
		long processId;
		boolean isAlive;
		for (final ProcessAliveDTO processAliveChange : processAliveChanges) {
			processId = processAliveChange.getId();
			isAlive = processAliveChange.getIsAlive();
			messageBuilder = new StringBuilder(256).append("El proceso ").append(processNamesMap.get(processId));
			if (processAliveChange.getIsAlive()) {
				messageBuilder.append(" se encuentra disponible nuevamente.");
			} else if (StringUtils.isEmpty(processAliveChange.getMessage())) {
				messageBuilder.append(" no responde.");
			} else {
				messageBuilder.append(" tiene un error. Razón: ").append(processAliveChange.getMessage());
			}
			notification = new Notification(null, isAlive, false, false, messageBuilder.toString(),
					Date.from(keepAliveDate));
			notification.setGateway(gateway);
			notification.setProcess(new Process(processId, null, processNamesMap.get(processId), isAlive));
			baseNotifications.add(notification);
			// Update Process status in the Database
			processRepository.updateProcessAliveStatus(processId, isAlive);
		}
		// Create each notification for each interested user.
		final List<Notification> notifications = new ArrayList<>();
		final MultiValueMap<AppUser, Notification> notificationsByUserMap = new LinkedMultiValueMap<>();
		for (final Notification baseNotification : baseNotifications) {
			for (final AppUser user : gatewayOwners) {
				notification = baseNotification.cloneForUser(user);
				notifications.add(notification);
				notificationsByUserMap.add(user, notification);
			}
		}
		// Insert all the notifications.
		notificationRepository.saveAll(notifications);
		return notificationsByUserMap;
	}

	@Override
	public List<TopicDTO> getAssociatedTopics(final long gatewayId) {
		final List<TopicDTO> topics = repository.getAssociatedTopics(gatewayId).stream()
				.map(r -> new TopicDTO(((BigInteger) r[0]).toString(), (String) r[1])).collect(Collectors.toList());
		if (topics.isEmpty()) {
			throw new InvalidKeyException("El gateway no existe o no tiene procesos asociados");
		}
		return topics;
	}

	@Override
	public Gateway getGatewayByProcess(final Long processId) {
		final Gateway gateway = repository.findByProcessId(processId);
		if (gateway == null) {
			LOGGER.error("Proceso no encontrado.");
			throw new InternalException("Proceso no encontrado.");
		}
		return gateway;
	}

	@Override
	public List<Gateway> getGatewaysByProcesses(final List<Long> processesId) {
		final List<Gateway> gateways = repository.findByProcessIds(processesId);
		if (gateways.size() == 0) {
			LOGGER.error("Proceso no encontrado.");
			throw new InternalException("Proceso no encontrado.");
		}
		return gateways;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Gateway> getGatewaysByUserId(final long userId) {
		final AppUser user = userRepository.findById(userId)
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe"));

		return user.isAdmin() ? (List<Gateway>) repository.findAll(Utils.NAME_ASC_SORT)
				: repository.findByUserId(userId, Utils.NAME_ASC_SORT);
	}
}
