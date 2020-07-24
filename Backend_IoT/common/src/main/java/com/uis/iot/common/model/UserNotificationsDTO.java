package com.uis.iot.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNotificationsDTO {

	private AppUserDTO user;

	private List<NotificationDTO> notifications;

	public UserNotificationsDTO() {
		super();
	}

	public UserNotificationsDTO(AppUserDTO user, List<NotificationDTO> notifications) {
		this.user = user;
		this.notifications = notifications;
	}

	public AppUserDTO getUser() {
		return user;
	}

	public void setUser(AppUserDTO user) {
		this.user = user;
	}

	public List<NotificationDTO> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationDTO> notifications) {
		this.notifications = notifications;
	}

}
