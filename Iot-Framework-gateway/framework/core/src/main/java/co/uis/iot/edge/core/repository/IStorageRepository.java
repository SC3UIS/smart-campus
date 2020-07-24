package co.uis.iot.edge.core.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uis.iot.edge.core.persistence.Message;

/**
 * Repository used for operations over {@link Message} entity.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IStorageRepository extends MongoRepository<Message, Long> {

	/**
	 * Finds messages by it's process' id.
	 * 
	 * @param processId id of the Process in the Backend.
	 * @return a {@link List<Message>} that wraps the query result.
	 */
	public List<Message> findByProcessId(Long processId);

	/**
	 * Finds messages which have been sent or not by it's process' id.
	 * 
	 * @param processId   id of the Process in the Backend.
	 * @param alreadySent boolean to query the messages already sent or not.
	 * @return a {@link List<Message>} that wraps the query result.
	 */
	public List<Message> findByProcessIdAndAlreadySent(Long processId, boolean alreadySent);

	/**
	 * Delete all the messages of a specific process by their id and sent status.
	 * 
	 * @param processId   id of the Process in the Backend.
	 * @param alreadySent boolean to query the messages already sent or not.
	 * @return the amount of messages deleted.
	 */
	public Long deleteByProcessIdAndAlreadySent(Long processId, boolean alreadySent);

	/**
	 * Delete all the messages from the storage collection by their sent status.
	 * 
	 * @param alreadySent indicates whether the messages were already sent or not.
	 * @return the amount of messages deleted.
	 */
	public Long deleteByAlreadySent(boolean alreadySent);

}
