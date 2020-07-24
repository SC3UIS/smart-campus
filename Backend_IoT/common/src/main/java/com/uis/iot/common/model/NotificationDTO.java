package com.uis.iot.common.model;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the Notification as a Data Transfer Object.
 * 
 * @author Federico
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

	private Long id;

	private Long gatewayId;

	private String gatewayName;

	private Long userId;

	private Long processId;

	private String processName;

	private boolean alive;

	private boolean read;

	private boolean hidden;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date timestamp;

	private String message;

	public NotificationDTO() {
		super();
	}

	/**
	 * Creates an instance of {@link NotificationDTO}.
	 * 
	 * @param id        of the notification.
	 * @param gatewayId id of the Gateway.
	 * @param userId    id of the User.
	 * @param processId id of the Process.
	 * @param alive     indicates if the Gateway/Process is alive or not.
	 * @param read      indicates if the notification was read or not.
	 * @param hidden    indicates if the Notification was hidden by the user.
	 * @param timestamp {@link Date} when the notification was sent.
	 * @param message   body of the notification.
	 */
	public NotificationDTO(Long id, Long gatewayId, Long userId, Long processId, boolean alive, boolean read,
			boolean hidden, Date timestamp, String message) {
		this.id = id;
		this.gatewayId = gatewayId;
		this.userId = userId;
		this.processId = processId;
		this.alive = alive;
		this.read = read;
		this.timestamp = timestamp;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	@Override
	public String toString() {
		return "[" + id + "( " + timestamp + ", " + alive + ") gateway = " + gatewayName + ", process = " + processName
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, gatewayId, processId, timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof NotificationDTO) {
			final NotificationDTO other = (NotificationDTO) obj;
			return Objects.equals(id, other.getId()) && Objects.equals(userId, other.getUserId())
					&& Objects.equals(gatewayId, other.getGatewayId())
					&& Objects.equals(processId, other.getProcessId())
					&& Objects.equals(timestamp, other.getTimestamp());
		}
		return false;
	}

}
