package co.uis.iot.edge.core.persistence;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uis.iot.edge.common.model.MessageDTO;

/**
 * Represents a Message Document.
 * 
 * @author Camilo Gutiérrez
 *
 */
@Document(collection = "message")
public class Message {
	
	/**
	 * The id used by MongoDB.
	 */
	@Id
	private String id;
	
	/**
	 * Defines if the message was already sent or not.
	 * This is useful for detecting batch messages.
	 */
	private boolean alreadySent;
		
	/**
	 * The process' id.
	 */
	@Indexed(unique = false)
	private Long processId;
	
	/**
	 * The payload to be stored.
	 */
	private String payload;
	
	/**
	 * The datetime when the message was created.
	 */
	private Date timestamp;

	public Message() {
	}

	/**
	 * Creates an instance of a Message.
	 * 
	 * @param id          of the document.
	 * @param alreadySent indicates if the message was already sent or not.
	 * @param processId   id of the {@link Process} that owns the message.
	 * @param payload     data sent.
	 * @param timestamp   the datetime when the message was created.
	 */
	public Message(String id, boolean alreadySent, Long processId, String payload, Date timestamp) {
		super();
		this.id = id;
		this.alreadySent = alreadySent;
		this.processId = processId;
		this.payload = payload;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public boolean isAlreadySent() {
		return alreadySent;
	}

	public void setAlreadySent(boolean alreadySent) {
		this.alreadySent = alreadySent;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Maps the given {@link MessageDTO} to a Message.
	 * 
	 * @param dto to be mapped.
	 * @return the equivalent {@link Message}.
	 */
	public static Message ofDTO(MessageDTO dto) {
		return new Message(null, false, dto.getProcessId(), dto.getPayload(), dto.getTimestamp());
	}

	/**
	 * Maps the current Process to a {@link MessageDTO}.
	 * 
	 * @return the mapped {@link MessageDTO}
	 */
	public MessageDTO toDTO() {
		return new MessageDTO(null, processId, payload, timestamp);
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", alreadySent=" + alreadySent + ", processId=" + processId + ", payload="
				+ payload + ", timestamp=" + timestamp + "]";
	}

}
