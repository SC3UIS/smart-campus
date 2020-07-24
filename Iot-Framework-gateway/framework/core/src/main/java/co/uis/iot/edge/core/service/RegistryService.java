package co.uis.iot.edge.core.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mongodb.MongoException;

import co.uis.iot.edge.common.model.EConfigProperty;
import co.uis.iot.edge.common.model.EPropertyType;
import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.PropertyDTO;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.core.exception.DeviceAlreadyExistsException;
import co.uis.iot.edge.core.exception.DeviceNotFoundException;
import co.uis.iot.edge.core.exception.GatewayExistsException;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.jobs.StorageCleanerRunnable;
import co.uis.iot.edge.core.persistence.Property;
import co.uis.iot.edge.core.persistence.Registry;
import co.uis.iot.edge.core.repository.IRegistryRepository;
import co.uis.iot.edge.core.utils.PropertyUtil;

/**
 * Implementation of Process business layer (service).
 * 
 * @author Camilo Gutiérrez
 *
 */
@Service
public class RegistryService implements IRegistryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);

	@Autowired
	private IRegistryRepository repository;

	@Autowired
	private IStorageService storageService;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private IActiveMQService activeMQService;
	
	@Override
	public List<RegistryDTO> getRegistries(final EThingType type) {
		final List<Registry> registries = type == null ? repository.findAll() : repository.findByType(type);
		return registries.stream().map(Registry::toDTO).collect(Collectors.toList());
	}

	@Override
	public RegistryDTO getRegistriesByIdBackendAndType(final EThingType type, final Long idBackend) {
		return repository.findByIdBackendAndType(idBackend, type).orElseThrow(DeviceNotFoundException::new).toDTO();
	}

	@Override
	public RegistryDTO createRegistry(final RegistryDTO registryDTO) {
		try {
			if (registryDTO.getType() == EThingType.GATEWAY) {
				return createGateway(registryDTO);
			} else {
				return createSensorOrActuator(registryDTO);
			}
		} catch (MongoException m) {
			LOGGER.error("Error creating registry: {}", m.getMessage());
			throw new PersistenceException("An error occurred creating the Registry.", m);
		}
	}

	/**
	 * Creates a Gateway if this didn't exist before.
	 * 
	 * @param gatewayDTO the {@link Registry} as DTO to be created.
	 * @return a {@link RegistryDTO} with the reported properties.
	 * @throws GatewayExistsException if the Gateway already exists.
	 */
	private RegistryDTO createGateway(RegistryDTO gatewayDTO) {
		List<Registry> foundGateway = repository.findByType(EThingType.GATEWAY);
		if (!CollectionUtils.isEmpty(foundGateway)) {
			throw new GatewayExistsException();
		}
		gatewayDTO.setProperties(PropertyUtil.sanitizeProperties(gatewayDTO.getProperties()));

		if (gatewayDTO.getProperties().stream()
				.noneMatch(property -> property.getName().equals(EConfigProperty.BROKER_URL.toString())
						&& property.getType() == EPropertyType.CONFIG)) {
			throw new IllegalArgumentException("The property broker_url is null or empty");
		}

		Registry gatewayRegistry = Registry.ofDTO(gatewayDTO);
		gatewayRegistry.getProperties().addAll(PropertyUtil.getReportedProperties());
		repository.save(gatewayRegistry);

		Optional<PropertyDTO> brokerURL = PropertyUtil.getConfigPropertyByName(gatewayDTO.getProperties(),
				EConfigProperty.BROKER_URL);
		if (brokerURL.isPresent()) {
			connectToBroker(brokerURL.get().getValue());
		}

		updateDbCleaner(gatewayDTO);

		return gatewayRegistry.toDTO();
	}

	/**
	 * Updates a Gateway if exists.
	 * 
	 * @param gatewayDTO the {@link Registry} as DTO to be updated.
	 * @return a {@link RegistryDTO} with re-calculated reported properties.
	 * @throws DeviceNotFoundException if the Device doesn't exist.
	 */
	private RegistryDTO updateGateway(RegistryDTO gatewayDTO) {

		Registry foundRegistry = repository.findByIdBackendAndType(gatewayDTO.getId(), EThingType.GATEWAY)
				.orElseThrow(DeviceNotFoundException::new);

		Optional<String> newBrokerUrl = gatewayDTO.getProperties().stream()
				.filter(property -> property.is(EConfigProperty.BROKER_URL))
				.findFirst().map(PropertyDTO::getValue);
		if (!newBrokerUrl.isPresent() || StringUtils.isEmpty(newBrokerUrl.get())) {
			throw new IllegalArgumentException("The property BROKER_URL is null or empty");
		}

		foundRegistry.setIdBackend(gatewayDTO.getId());
		foundRegistry.setProperties(PropertyUtil.sanitizeProperties(gatewayDTO.getProperties()).stream()
				.map(Property::ofDTO).collect(Collectors.toList()));
		foundRegistry.setName(gatewayDTO.getName());
		foundRegistry.setType(gatewayDTO.getType());
		foundRegistry.setDescription(gatewayDTO.getDescription());
		foundRegistry.getProperties().addAll(PropertyUtil.getReportedProperties());
		repository.save(foundRegistry);

		Optional<String> oldBrokerUrl = foundRegistry.getProperties().stream().filter(PropertyUtil::isBrokerUrlProperty)
				.findFirst().map(Property::getValue);

		if (!oldBrokerUrl.isPresent() || !newBrokerUrl.equals(oldBrokerUrl)) {
			connectToBroker(newBrokerUrl.get());
		}

		updateDbCleaner(gatewayDTO);

		return foundRegistry.toDTO();
	}

	@Override
	public RegistryDTO updateRegistry(final RegistryDTO registryDTO) {
		try {
			if (registryDTO.getType() == EThingType.GATEWAY) {
				return updateGateway(registryDTO);
			} else {
				return updateSensorOrActuator(registryDTO);
			}
		} catch (MongoException m) {
			LOGGER.error("Error updating registry: {}", m.getMessage());
			throw new PersistenceException("An error occurred updating the Registry.", m);
		}
	}

	@Override
	public void deleteRegistry(final Long id, final EThingType type) {
		try {
			final Long deletedCount = repository.deleteByIdBackendAndType(id, type);
			Assert.isTrue(deletedCount == 1L, "Registry not found.");
			if (type == EThingType.GATEWAY && storageService.getScheduledDbCleanup() != null) {
				storageService.getScheduledDbCleanup().cancel(true);
				storageService.setScheduledDbCleanup(null);
				activeMQService.disconnectFromBroker();
			}
		} catch (MongoException m) {
			LOGGER.error("Error deleting registry: {}", m.getMessage());
			throw new PersistenceException("An error occurred deleting the Registry.", m);
		}
	}

	/**
	 * Creates a Sensor or Actuator if didn't exist before.
	 * 
	 * @param registryDTO the {@link Registry} as DTO to be created.
	 * @return the {@link RegistryDTO} with its properties sanitized.
	 * @throws DeviceAlreadyExistsException if the Registry already exists.
	 */
	private RegistryDTO createSensorOrActuator(final RegistryDTO registryDTO) {
		if (repository.findByIdBackendAndType(registryDTO.getId(), registryDTO.getType()).isPresent()) {
			throw new DeviceAlreadyExistsException();
		}
		registryDTO.setProperties(PropertyUtil.sanitizeProperties(registryDTO.getProperties()));
		repository.save(Registry.ofDTO(registryDTO));
		return registryDTO;
	}

	/**
	 * Updates a Sensor or Actuator if exists.
	 * 
	 * @param registryDTO the Sensor or Actuator to be updated.
	 * @return the {@link RegistryDTO} with its properties sanitized.
	 * @throws DeviceNotFoundException if the Registry doesn't exist.
	 */
	private RegistryDTO updateSensorOrActuator(final RegistryDTO registryDTO) {
		Registry foundRegistry = repository.findByIdBackendAndType(registryDTO.getId(), registryDTO.getType())
				.orElseThrow(DeviceNotFoundException::new);
		foundRegistry.setIdBackend(registryDTO.getId());
		foundRegistry.setName(registryDTO.getName());
		foundRegistry.setProperties(PropertyUtil.sanitizeProperties(registryDTO.getProperties()).stream()
				.map(Property::ofDTO).collect(Collectors.toList()));
		foundRegistry.setType(registryDTO.getType());
		foundRegistry.setDescription(registryDTO.getDescription());
		repository.save(foundRegistry);
		return foundRegistry.toDTO();
	}

	@Override
	public void initDbCleanerTasks() {
		final List<RegistryDTO> registries = getRegistries(EThingType.GATEWAY);

		if (CollectionUtils.isEmpty(registries)) {
			return;
		}

		updateDbCleaner(registries.get(0));
	}

	@Override
	public RegistryDTO getGateway() {
		final List<RegistryDTO> registries = getRegistries(EThingType.GATEWAY);
		if (CollectionUtils.isEmpty(registries)) {
			return null;
		}
		return registries.get(0);
	}

	/**
	 * Updates the db cleaner task.
	 * 
	 * @param gatewayDTO gateway object.
	 */
	private void updateDbCleaner(final RegistryDTO gatewayDTO) {
		if (storageService.getScheduledDbCleanup() != null) {
			storageService.getScheduledDbCleanup().cancel(true);
			storageService.setScheduledDbCleanup(null);
			LOGGER.info("The Db Cleaner task was canceled.");
		}
		final Optional<PropertyDTO> dbCleanupTime = PropertyUtil.getConfigPropertyByName(gatewayDTO.getProperties(),
				EConfigProperty.DB_CLEANUP_TIME);
		if (dbCleanupTime.isPresent()) {
			final Long period = Long.valueOf(dbCleanupTime.get().getValue()) * 60 * 1000;
			storageService.setScheduledDbCleanup(
					taskScheduler.scheduleAtFixedRate(new StorageCleanerRunnable(storageService), period));
			LOGGER.info("The Db Cleaner task was configured every {} ms.", period);
		}
	}

	@Override
	public void initBrokerConnection() {
		final List<Registry> foundGateway = repository.findByType(EThingType.GATEWAY);
		if (CollectionUtils.isEmpty(foundGateway)) {
			LOGGER.error("The broker is not configured yet");
			return;
		}
		final List<Property> properties = foundGateway.get(0).getProperties().stream()
				.filter(PropertyUtil::isBrokerUrlProperty).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(properties)) {
			LOGGER.error(
					"The existing gateway has not BROKER properties configured. Connection to the broker can't be established and messages can't be sent.");
			return;
		}

		final String brokerUrl = properties.get(0).getValue();

		connectToBroker(brokerUrl);
	}

	/**
	 * Reconnects the gateway to a broker.
	 * 
	 * @param brokerURL
	 */
	private void connectToBroker(String brokerURL) {
		activeMQService.connectToBroker(brokerURL);
	}
}
