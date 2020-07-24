package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.uis.iot.common.model.NotificationDTO;

@Entity
@NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n")
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_notification")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_gateway", nullable = false)
	private Gateway gateway;

	@ManyToOne
	@JoinColumn(name = "id_user", nullable = false)
	private AppUser user;

	@ManyToOne
	@JoinColumn(name = "id_process", nullable = true)
	private Process process;

	private boolean alive;

	@Column(name = "was_read")
	private boolean read;

	private boolean hidden;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sent_date")
	private Date timestamp;

	private String message;

	public Notification() {
		super();
	}

	public Notification(Long id, boolean alive, boolean read, boolean hidden, String message, Date timestamp) {
		super();
		this.id = id;
		this.alive = alive;
		this.read = read;
		this.hidden = hidden;
		this.message = message;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void getId(Long id) {
		this.id = id;
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
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

	public NotificationDTO toDTO() {
		final NotificationDTO notification = new NotificationDTO(id, gateway.getId(), user.getId(),
				process != null ? process.getId() : null, alive, read, hidden, timestamp, message);
		notification.setGatewayName(gateway.getName());
		if (process != null) {
			notification.setProcessName(process.getName());
		}
		return notification;
	}

	public static Notification fromDTO(NotificationDTO dto) {
		final Notification notification = new Notification(dto.getId(), dto.isAlive(), dto.isRead(), dto.isHidden(),
				dto.getMessage(), dto.getTimestamp());
		notification.setGateway(new Gateway(dto.getGatewayId(), null, null, null, true));
		if (dto.getProcessId() != null) {
			notification.setProcess(new Process(dto.getProcessId(), null, null, true));
		}
		notification.setUser(new AppUser(dto.getUserId(), null, false, null, null, null));

		return notification;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", gateway=" + gateway + ", user=" + user + ", process=" + process + ", alive=" + alive
				+ "]";
	}

	/**
	 * Clones a notification for a specific user.
	 * 
	 * @param user owner of the new notification.
	 * @return a new Notification.
	 */
	public Notification cloneForUser(final AppUser user) {
		final Notification clone = new Notification(id, alive, read, hidden, message, timestamp);
		clone.setUser(user);
		clone.setGateway(gateway);
		clone.setProcess(process);
		return clone;
	}

}
