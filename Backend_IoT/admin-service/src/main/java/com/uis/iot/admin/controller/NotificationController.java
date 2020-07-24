package com.uis.iot.admin.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uis.iot.admin.entity.Notification;
import com.uis.iot.admin.service.INotificationService;
import com.uis.iot.common.model.NotificationDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Entry point for all Notification related REST services.
 * 
 * @author Federico
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private INotificationService notificationService;

	private static final String GATEWAY_ID_REQUIRED = "Id de gateway requerido.";

	private static final String USER_ID_REQUIRED = "Id de usuario requerido.";

	private static final String TIMESTAMP_REQUIRED = "Fecha de la notificación requerida";

	private static final String MESSAGE_REQUIRED = "Mensaje requerido.";

	private static final String DELETE_SUCCESSFUL = "Notificaciones eliminadas correctamente";

	/**
	 * Creates a new Notification.
	 * 
	 * @param notificationDTO the notification to be created.
	 * @return a {@link ResponseEntity} that contains the notification created.
	 */
	@PostMapping(path = "/notification", consumes = "application/json", produces = "application/json")
	public ResponseEntity<NotificationDTO> register(@RequestBody final NotificationDTO notificationDTO) {
		Assert.notNull(notificationDTO.getGatewayId(), GATEWAY_ID_REQUIRED);
		Assert.notNull(notificationDTO.getUserId(), USER_ID_REQUIRED);
		Assert.hasText(notificationDTO.getMessage(), MESSAGE_REQUIRED);

		return new ResponseEntity<>(
				notificationService.createNotification(Notification.fromDTO(notificationDTO)).toDTO(),
				HttpStatus.CREATED);
	}

	/**
	 * Updates a given notification.
	 * 
	 * @param notificationId  id of the notification to be updated.
	 * @param notificationDTO notification to be updated.
	 * @return a {@link ResponseEntity} that contains the notification updated.
	 */
	@PutMapping(path = "/notification/{notificationId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<NotificationDTO> edit(@PathVariable long notificationId,
			@RequestBody final NotificationDTO notificationDTO) {
		Assert.notNull(notificationDTO.getGatewayId(), GATEWAY_ID_REQUIRED);
		Assert.notNull(notificationDTO.getUserId(), USER_ID_REQUIRED);
		Assert.notNull(notificationDTO.getTimestamp(), TIMESTAMP_REQUIRED);
		Assert.hasText(notificationDTO.getMessage(), MESSAGE_REQUIRED);

		notificationDTO.setId(notificationId);
		return new ResponseEntity<>(
				notificationService.updateNotification(Notification.fromDTO(notificationDTO)).toDTO(), HttpStatus.OK);
	}

	/**
	 * Marks notifications as read.
	 * 
	 * @param userId           id of the user owner of the notifications.
	 * @param notificationsIds {@link Set} of notifications ids.
	 * @return a {@link ResponseEntity} that indicates that the operation succeeded.
	 */
	@PutMapping(path = "/user/{userId}/read", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseDTO> markNotificationsAsRead(@PathVariable long userId,
			@RequestBody final Set<Long> notificationsIds) {
		Assert.notNull(notificationsIds, "Ids de notificaciones no puede ser nulo.");
		notificationService.markNotificationsAsRead(userId, notificationsIds);
		return new ResponseEntity<>(new ResponseDTO("Notificaciones marcadas como leídas correctamente.", true),
				HttpStatus.OK);
	}

	/**
	 * Deletes a notification identified with a given id. This is not a deletion but
	 * instead hides the notification.
	 * 
	 * @param notificationId id of the notification.
	 * @return a {@link ResponseEntity} that indicates that the operation succeeded.
	 */
	@DeleteMapping(path = "/notification/{notificationId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseDTO> deleteNotification(@PathVariable long notificationId) {
		notificationService.hideNotification(notificationId);
		return new ResponseEntity<>(new ResponseDTO(DELETE_SUCCESSFUL, true), HttpStatus.OK);
	}

	/**
	 * Retrieves the notifications of a specific user using lazy loading. If the
	 * page or count is not set all the items are retrieved.
	 * 
	 * @param userId id of the user owner of the notifications.
	 * @param count  amount of notifications to be retrieved.
	 * @param page   Page of notifications.
	 * @param all    <code>true</code> to return all the notifications for the user,
	 *               or <code>false</code> to retrieve only the ones that are not
	 *               hidden.
	 * @return the list of notifications or an empty list if none exists.
	 */
	@GetMapping(path = "/user/{userId}", produces = "application/json")
	public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable long userId,
			@RequestParam(required = false) Integer count, @RequestParam(required = false) Integer page,
			@RequestParam(required = false, defaultValue = "false") boolean all) {
		if (page == null || count == null) {
			page = 0;
			count = Integer.MAX_VALUE;
		}
		return new ResponseEntity<>(notificationService
				.getNotificationsByUserId(userId, all, PageRequest.of(page, count, Sort.by("timestamp").descending()))
				.stream().map(Notification::toDTO).collect(Collectors.toList()), HttpStatus.OK);
	}

	/**
	 * Retrieves the amount of unread notifications of an specific user.
	 * 
	 * @param userId id of the user owner of the notifications.
	 * @return the count of notifications.
	 */
	@GetMapping(path = "/user/{userId}/count", produces = "application/json")
	public ResponseEntity<Long> getUnreadNotificationsCount(@PathVariable long userId) {
		return new ResponseEntity<>(notificationService.getUnreadNotificationsCount(userId), HttpStatus.OK);
	}

}
