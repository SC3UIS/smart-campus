package co.uis.iot.edge.core.service;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import co.uis.iot.edge.common.model.MessageDTO;

/**
 * Communication business layer.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface ICommunicationService {

	/**
	 * Sends messages to a broker.
	 * 
	 * @param messageDTO message to be sent.
	 */
	public void sendActiveMQMessage(MessageDTO messageDTO);

	/**
	 * Sends batch messages of a specific process.
	 * 
	 * @param processId the process' id.
	 */
	public void sendActiveMQBatch(Long processId);

	/**
	 * Gets the processes' batch map.
	 *
	 * @return the {@link Map<Long, ScheduledFuture>} with all the scheduled
	 *         processes tasks.
	 */
	public Map<Long, ScheduledFuture<?>> getBatchProcesses();

}
