package co.uis.iot.edge.core.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;
import co.uis.iot.edge.core.persistence.Message;
import co.uis.iot.edge.core.repository.IProcessRepository;
import co.uis.iot.edge.core.repository.IStorageRepository;
import co.uis.iot.edge.core.utils.Util;

/**
 * Implementation of Storage business layer (service).
 * 
 * @author Camilo Gutiérrez
 *
 */
@Service
public class StorageService implements IStorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

	@Autowired
	private IStorageRepository storageRepository;

	@Autowired
	private IProcessRepository processRepository;
	
	@Autowired
	private IRegistryService registryService;

	private ScheduledFuture<?> scheduledDbCleanup;

	@Override
	public List<MessageDTO> getMessagesByProcessId(Long processId) {
		final RegistryDTO gateway = registryService.getGateway();
		return storageRepository.findByProcessId(processId).stream().map(message -> {
			message.setTimestamp(Util.utcToLocalDate(message.getTimestamp()));
			MessageDTO messageDTO = message.toDTO();
			messageDTO.setGatewayId(gateway.getId());
			return messageDTO;
		}).collect(Collectors.toList());
	}

	@Override
	public List<Message> getMessagesByProcessIdAndSentStatus(Long processId, boolean alreadySent) {
		return storageRepository.findByProcessIdAndAlreadySent(processId, alreadySent).stream().map(message -> {
			message.setTimestamp(Util.utcToLocalDate(message.getTimestamp()));
			return message;
		}).collect(Collectors.toList());
	}

	@Override
	public void deleteMessages(long processId, boolean alreadySent) {
		try {
			storageRepository.deleteByProcessIdAndAlreadySent(processId, alreadySent);
		} catch (MongoException m) {
			LOGGER.error("Error deleting the messages: {}", m.getMessage());
			throw new PersistenceException("An error occurred deleting the messages.", m);
		}
	}

	@Override
	public void deleteMessagesBySentStatus(boolean alreadySent) {
		try {
			storageRepository.deleteByAlreadySent(alreadySent);
		} catch (MongoException m) {
			LOGGER.error("Error deleting the messages: {}", m.getMessage());
			throw new PersistenceException("An error occurred deleting the messages.", m);
		}
	}

	@Override
	public void saveMessage(MessageDTO messageDTO, boolean alreadySent) {
		try {
			if (messageDTO.getTimestamp() == null) {
				messageDTO.setTimestamp(new Date());
			}

			if (!processRepository.findByIdBackend(messageDTO.getProcessId()).isPresent()) {
				throw new ProcessNotFoundException("The given process doesn't exist.");
			}
			Message message = Message.ofDTO(messageDTO);
			message.setAlreadySent(alreadySent);
			storageRepository.save(message);
		} catch (MongoException m) {
			LOGGER.error("Error saving the message: {}", m.getMessage());
			throw new PersistenceException("An error occurred saving the message.", m);
		}
	}

	@Override
	public void updateMessages(List<Message> messages) {
		try {
			List<Message> updatedMessages = messages.stream().map(message -> {
				message.setAlreadySent(true);
				return message;
			}).collect(Collectors.toList());
			storageRepository.saveAll(updatedMessages);
		} catch (MongoException m) {
			LOGGER.error("Error updating the message: {}", m.getMessage());
			throw new PersistenceException("An error occurred updating the messages.", m);
		}
	}

	@Override
	public ScheduledFuture<?> getScheduledDbCleanup() {
		return scheduledDbCleanup;
	}

	@Override
	public void setScheduledDbCleanup(ScheduledFuture<?> scheduledDbCleanup) {
		this.scheduledDbCleanup = scheduledDbCleanup;
	}
}
