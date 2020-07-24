package co.uis.iot.edge.core.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import co.uis.iot.edge.common.model.EConfigProperty;
import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.PropertyDTO;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.core.exception.DeviceNotFoundException;
import co.uis.iot.edge.core.exception.MissingTopicException;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;
import co.uis.iot.edge.core.persistence.Message;
import co.uis.iot.edge.core.utils.PropertyUtil;
import co.uis.iot.edge.core.utils.Util;

/**
 * Implementation of Communication business layer (service).
 * 
 * @author Camilo Gutiérrez
 *
 */
@Service
public class CommunicationService implements ICommunicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationService.class);

	private Map<Long, ScheduledFuture<?>> batchProcesses = new HashMap<>();

	@Autowired
	private IActiveMQService activeMQService;

	@Autowired
	private IProcessService processService;

	@Autowired
	private IStorageService storageService;

	@Autowired
	private IRegistryService registryService;

	@Override
	public void sendActiveMQMessage(MessageDTO messageDTO) {
		final Long processId = messageDTO.getProcessId();
		final RegistryDTO gateway = registryService.getGateway();
		if (gateway == null) {
			throw new DeviceNotFoundException("Message can't be sent. The Gateway is not configured.");
		}
		final Long gatewayId = gateway.getId();
		final ProcessDTO process = processService.getProcessByIdBackend(processId);
		if (process == null) {
			throw new ProcessNotFoundException("Message can't be sent. The Process doesn't exist.");
		}
		final String topic = PropertyUtil.getTopicIfExists(process);
		if (StringUtils.isEmpty(topic)) {
			throw new MissingTopicException("Message can't be sent. The Topic isn't configured.");
		}
		messageDTO.setTimestamp(new Date());
		messageDTO.setGatewayId(gatewayId);
		final PropertyDTO sendConfigProperty = PropertyUtil.getSendTypeProperty(process).orElse(null);
		if (sendConfigProperty == null) {
			activeMQService.sendMessageToQueue(String.valueOf(gatewayId), Util.mapToJson(Arrays.asList(messageDTO)));
		} else if (sendConfigProperty.is(EConfigProperty.BATCH_AMOUNT)) {
			final List<Message> storedMessages = storageService.getMessagesByProcessIdAndSentStatus(processId, false);
			if (storedMessages.size() == Integer.valueOf(sendConfigProperty.getValue()) - 1) {
				storedMessages.add(Message.ofDTO(messageDTO));
				storageService.updateMessages(storedMessages);
				activeMQService.sendMessageToQueue(String.valueOf(gatewayId), mapMessages(storedMessages, gatewayId));
				LOGGER.info("Batch amount process with id {} was sent successfully.", processId);
				return;
			}
		}

		// Store the message if it's BATCH FREQUENCY or SEND NOW.
		storageService.saveMessage(messageDTO, sendConfigProperty == null);
	}

	@Override
	public void sendActiveMQBatch(Long processId) {
		final RegistryDTO gateway = registryService.getGateway();
		if (gateway == null) {
			LOGGER.info("Batch frequency process with id {} can't be sent. The Gateway is not configured.", processId);
			return;
		}
		final Long gatewayId = gateway.getId();
		String topic = PropertyUtil.getTopicIfExists(processService.getProcessByIdBackend(processId));
		if (StringUtils.isEmpty(topic)) {
			LOGGER.info("Batch frequency process with id {} can't be sent. The Topic is not configured.", processId);
			return;
		}

		try {
			final List<Message> storedMessages = storageService.getMessagesByProcessIdAndSentStatus(processId, false);
			if (CollectionUtils.isEmpty(storedMessages)) {
				LOGGER.info("Batch frequency process with id {} wasn't sent. No pending messages.", processId);
				return;
			}

			storageService.updateMessages(storedMessages);
			activeMQService.sendMessageToQueue(String.valueOf(gatewayId), mapMessages(storedMessages, gatewayId));
			LOGGER.info("Batch frequency process with id {} was sent successfully.", processId);
		} catch (PersistenceException ex) {
			LOGGER.error("Batch frequency process with id {} couldn't be sent.", processId, ex);
		}
	}

	@Override
	public Map<Long, ScheduledFuture<?>> getBatchProcesses() {
		return batchProcesses;
	}

	/**
	 * Maps the passed messages to DTOs.
	 * 
	 * @param messages to be mapped.
	 * @return the mapped Messages list as JSON.
	 */
	private String mapMessages(List<Message> messages, Long gatewayId) {
		return Util.mapToJson(messages.stream().map(message -> {
			MessageDTO messageDTO = message.toDTO();
			messageDTO.setGatewayId(gatewayId);
			return messageDTO;
		}).collect(Collectors.toList()));
	}

}