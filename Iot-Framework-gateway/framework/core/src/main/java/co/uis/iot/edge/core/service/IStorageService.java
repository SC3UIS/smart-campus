package co.uis.iot.edge.core.service;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;
import co.uis.iot.edge.core.persistence.Message;

/**
 * Business layer to handle the Storage of things' data.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IStorageService {

	/**
	 * Retrieves all the messages for a given process id.
	 * 
	 * @param processId to filter the messages.
	 * @return the found messages.
	 */
	public List<MessageDTO> getMessagesByProcessId(Long processId);
	
	/**
	 * Retrieves all the messages for a given process id.
	 * 
	 * @param processId to filter the messages.
	 * @param alreadySent indicates if the message was already sent.
	 * @return the found messages.
	 */
	public List<Message> getMessagesByProcessIdAndSentStatus(Long processId, boolean alreadySent);
	
	/**
	 * Deletes all messages for a given process and sent status.
	 * 
	 * @param processId   of the message to be sent.
	 * @param alreadySent indicates if the message was already sent.
	 * @throws PersistenceException if the deletion was not successful.
	 */
	public void deleteMessages(long processId, boolean alreadySent);
	
	/**
	 * Deletes all messages by sent status.
	 * 
	 * @param alreadySent indicates if the message was already sent.
	 * @throws PersistenceException if the deletion was not successful.
	 */
	public void deleteMessagesBySentStatus(boolean alreadySent);

	/**
	 * Saves the given Message.
	 * 
	 * @param message     to be inserted.
	 * @param alreadySent indicates if the message was already sent.
	 * @throws PersistenceException     if the operation was not successful.
	 * @throws ProcessNotFoundException if the process wasn't found.
	 */
	public void saveMessage(MessageDTO messageDTO, boolean alreadySent);
	
	/**
	 * Updates the given Messages.
	 * 
	 * @param messages to be updated.
	 * @throws PersistenceException if the operation was not successful.
	 * 
	 */
	public void updateMessages(List<Message> message);
	
	/**
	 * Allows to get the db cleanup task.
	 * 
	 * @return a {@link @ScheduledFuture<?>}.
	 */
	public ScheduledFuture<?> getScheduledDbCleanup();

	/**
	 * Allows to updated the db cleanup task.
	 * @param scheduledDbCleanup
	 */
	public void setScheduledDbCleanup(ScheduledFuture<?> scheduledDbCleanup);

}
