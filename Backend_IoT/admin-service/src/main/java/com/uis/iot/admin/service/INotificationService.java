package com.uis.iot.admin.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.uis.iot.admin.entity.Notification;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;

/**
 * Definition of {@link Notification} related business logic.
 * 
 * @author Federico
 *
 */
public interface INotificationService {

	/**
	 * Creates a new {@link Notification}.
	 * 
	 * @param notification the notification to be created, not <code>null</code>.
	 * @return the notification with its respective id after it was created.
	 * @throws InternalException if the creation of the Notification failed.
	 */
	public Notification createNotification(final Notification notification);

	/**
	 * Updates a {@link Notification} if exists.
	 * 
	 * @param notification the notification to be updated, not <code>null</code>.
	 * @return the notification with the information updated.
	 * @throws InternalException if any persistence operation failed.
	 */
	public Notification updateNotification(final Notification notification);

	/**
	 * Marks a list of notifications of a specific as read.
	 * 
	 * @param userId           id of the user owner of the notifications.
	 * @param notificationsIds the ids of the notifications to be marked as read.
	 * @throws InternalException if any persistence operation failed.
	 */
	public void markNotificationsAsRead(final Long userId, final Set<Long> notificationsIds);

	/**
	 * Hides a notification with a given id.
	 * 
	 * @param notificationId id of the notification to be hidden.
	 * @throws InternalException if any persistence operation failed.
	 */
	public void hideNotification(final long notificationId);

	/**
	 * Gets the amount of unread notifications for a specific user.
	 * 
	 * @param userId id of the user owner of the notifications.
	 * @return the amount of unread notifications.
	 */
	public long getUnreadNotificationsCount(final Long userId);

	/**
	 * Retrieves the notifications of a specific user using a {@link Pageable} to
	 * sort or lazy load the notifications.
	 * 
	 * @param userId id of the user owner of the notifications.
	 * @param all    <code>true</code> to return all the notifications,
	 *               <code>false</code> to return only the visible ones.
	 * @param page   {@link Pageable} to execute sort/paging.
	 * @return the notifications of the user that belong to the given page.
	 * @throws InvalidKeyException if the user doesn't exist.
	 */
	public List<Notification> getNotificationsByUserId(final Long userId, final boolean all, final Pageable page);
}
