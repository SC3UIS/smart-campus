package co.uis.iot.edge.core.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mongodb.MongoException;

import co.uis.iot.edge.common.model.EConfigProperty;
import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.PropertyDTO;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.common.model.ResponseDTO;
import co.uis.iot.edge.core.exception.MissingTopicException;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessAlredyExistException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;
import co.uis.iot.edge.core.jobs.MessageSenderRunnable;
import co.uis.iot.edge.core.persistence.Process;
import co.uis.iot.edge.core.persistence.Property;
import co.uis.iot.edge.core.repository.IProcessRepository;
import co.uis.iot.edge.core.utils.PropertyUtil;
import co.uis.iot.edge.core.vertx.IVertxHandler;

/**
 * Implementation of Process business layer (service).
 * 
 * @author Camilo Gutiérrez
 *
 */
@Service
@CacheConfig(cacheNames = "processes")
public class ProcessService implements IProcessService {

	@Autowired
	private IProcessRepository repository;
	
	@Autowired
	private ICommunicationService communicationService;
	
	@Autowired
	private TaskScheduler taskScheduler;
	
	@Autowired
	private IRegistryService registryService;

	@Autowired
	private IVertxHandler vertxHandler;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

	@Override
	public List<ProcessDTO> getProcesses() {
		return repository.findAll().stream().map(Process::toDTO).collect(Collectors.toList());
	}

	@Override
	@Cacheable(key = "#idBackend", sync = true)
	public ProcessDTO getProcessByIdBackend(Long idBackend) {
		return repository.findByIdBackend(idBackend)
				.orElseThrow(ProcessNotFoundException::new).toDTO();
	}

	@Override
	public List<ProcessDTO> getProcessByDeployedStatus(boolean deployed) {
		return repository.findByDeployed(deployed).stream().map(Process::toDTO).collect(Collectors.toList());
	}

	@Override
	@Cacheable(key = "#processDTO.id", sync = true)
	public ProcessDTO createProcess(ProcessDTO processDTO) {
		try {
			if (repository.findByIdBackend(processDTO.getId()).isPresent()) {
				throw new ProcessAlredyExistException();
			}
			processDTO.setProperties(PropertyUtil.sanitizeProperties(processDTO.getProperties()));
			if (processDTO.getProperties().stream().noneMatch(property -> property.is(EConfigProperty.TOPIC))) {
				throw new MissingTopicException();
			}
			repository.save(Process.ofDTO(processDTO));
			
			updateBatchProcesses(processDTO);
			
			return processDTO;
		} catch (MongoException m) {
			LOGGER.error("Error inserting the process: {}", m.getMessage());
			throw new PersistenceException("An error occurred creating the Process.", m);
		}
	}

	@Override
	@CachePut(key = "#processDTO.id")
	public ProcessDTO updateProcess(ProcessDTO processDTO) {
		try {
			Process foundProcess = repository.findByIdBackend(processDTO.getId())
					.orElseThrow(ProcessNotFoundException::new);

			foundProcess.setProperties(PropertyUtil.sanitizeProperties(processDTO.getProperties()).stream()
					.map(Property::ofDTO).collect(Collectors.toList()));

			if (processDTO.getProperties().stream().noneMatch(property -> property.is(EConfigProperty.TOPIC))) {
				throw new IllegalArgumentException("Topic is required.");
			}

			foundProcess.setDescription(processDTO.getDescription());
			foundProcess.setName(processDTO.getName());

			repository.save(foundProcess);
			
			updateBatchProcesses(processDTO);
			
			return processDTO;
		} catch (MongoException m) {
			LOGGER.error("Error updating the process: {}", m.getMessage());
			throw new PersistenceException("An error occurred updating the Process.", m);
		}
	}

	@Override
	@CachePut(key = "#processId")
	public ProcessDTO updateProcessDeployStatus(Long processId, boolean deployed) {
		try {
			Process foundProcess = repository.findByIdBackend(processId).orElseThrow(ProcessNotFoundException::new);
			foundProcess.setDeployed(deployed);
			repository.save(foundProcess);
			return foundProcess.toDTO();
		} catch (MongoException m) {
			LOGGER.error("Error updating the process: {}", m.getMessage());
			return null;
		}
	}

	@Override
	@CacheEvict(key = "#idBackend")
	public void deleteProcess(Long idBackend) {
		try {
			if (repository.deleteByIdBackend(idBackend) != 1L) {
				throw new ProcessNotFoundException();
			}
			final ScheduledFuture<?> scheduledProcess = communicationService.getBatchProcesses().remove(idBackend);
			if (scheduledProcess != null) {
				scheduledProcess.cancel(true);
			}
			// undeploy the jar
			vertxHandler.deployProcess(idBackend, null, false);

		} catch (MongoException m) {
			LOGGER.error("Error deleting the process: {}", m.getMessage());
			throw new PersistenceException("An error occurred deleting the process.", m);
		}
	}
	
	@Override
	public void initBatchFrequencyTasks() {
		List<RegistryDTO> gateway = registryService.getRegistries(EThingType.GATEWAY);
		
		if (CollectionUtils.isEmpty(gateway)) {
			LOGGER.info("Batch frequency processes can't be started as the Gateway is not configured yet.");
			return;
		}

		List<ProcessDTO> processesDTO = getProcesses();

		if (CollectionUtils.isEmpty(processesDTO)) {
			return; // no processes.
		}

		for (ProcessDTO processDTO : processesDTO) {
			try {
				updateBatchProcesses(processDTO);
			} catch (IllegalArgumentException e) {
				LOGGER.info("Batch frequency process with id {} can't be started.", processDTO.getId(), e);
			}
		}
	}
	
	@Override
	public void initDeployedProcesses() {
		PropertyDTO processJarProperty;
		for (ProcessDTO process : getProcessByDeployedStatus(true)) {
			processJarProperty = PropertyUtil.getProcessJarProperty(process).orElse(null);
			if (processJarProperty == null || StringUtils.isEmpty(processJarProperty.getValue())) {
				continue;
			}
			LOGGER.info("Deploying JAR for Process {} ({})", process.getId(), processJarProperty.getValue());
			vertxHandler.deployProcess(process.getId(), processJarProperty.getValue(), true);
		}
	}

	@Override
	public ResponseDTO deployProcess(long processId, boolean deploy) {
		final ProcessDTO process = getProcessByIdBackend(processId);
		final PropertyDTO processJar = PropertyUtil.getProcessJarProperty(process).orElse(null);
		if (processJar == null || StringUtils.isEmpty(processJar.getValue().trim())) {
			// not configured then ignore the deploy.
			return new ResponseDTO("The given process doesn't have the property process_jar configured.", false);
		}
		return vertxHandler.deployProcess(processId, processJar.getValue(), deploy);
	}

	/**
	 * Updates the batch processes of scheduled tasks.
	 * 
	 * @param processDTO process' object.
	 * @throws IllegalArgumentException if Batch Frequency value isn't a number.
	 */
	private void updateBatchProcesses(ProcessDTO processDTO) {
		ScheduledFuture<?> scheduledProcess = communicationService.getBatchProcesses().remove(processDTO.getId());
		if (scheduledProcess != null) {
			scheduledProcess.cancel(true);
			LOGGER.info("Batch frequency process with id {} was unscheduled successfully.",
					processDTO.getId());
		}
		Optional<PropertyDTO> batchFrequence = PropertyUtil.getConfigPropertyByName(processDTO.getProperties(),
				EConfigProperty.BATCH_FREQUENCY);
		if (batchFrequence.isPresent()) {
			try {
				Long period = Long.valueOf(batchFrequence.get().getValue()) * 60 * 1000;
				communicationService.getBatchProcesses().put(processDTO.getId(), taskScheduler.scheduleAtFixedRate(
						new MessageSenderRunnable(processDTO.getId(), communicationService), period));
				LOGGER.info("Batch frequency process with id {} and period {} was scheduled successfully.",
						processDTO.getId(), period);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid Batch Frequency Process.", e);
			}
		}
	}

}
