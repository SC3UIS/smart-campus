package com.uis.iot.admin.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uis.iot.admin.entity.Notification;

/**
 * Executes database operations involving the {@link Notification} entity.
 * 
 * @author Federico
 *
 */
@Repository
public interface INotificationRepository extends PagingAndSortingRepository<Notification, Long> {

	/**
	 * Gets the notifications for a specific user using a {@link Pageable} page.
	 * 
	 * @param userId id of the user that owns the notifications.
	 * @param page   {@link Pageable} to paginate the results.
	 * @return the list of notifications, or an empty list if none was found.
	 */
	public List<Notification> findByUserId(long userId, Pageable page);

	/**
	 * Gets the notifications (visible) for a given user.
	 * 
	 * @param userId id of the user.
	 * @param page   {@link Pageable} to paginate the results.
	 * @return the list of notifications, or an empty list if none was found.
	 */
	public List<Notification> findByUserIdAndHiddenFalse(long userId, Pageable page);

	/**
	 * Retrieves the amount of unread notifications for a specific user, identified
	 * by its id.
	 * 
	 * @param userId id of the owner of the notifications.
	 * @return the amount of notifications.
	 */
	public long countByUserIdAndReadFalseAndHiddenFalse(long userId);

	/**
	 * Marks the given notifications (identified by its ids) for the given user as
	 * read.
	 * 
	 * @param userId           id of the owner of the notifications.
	 * @param notificationsIds a {@link Set} of ids of the notifications to be
	 *                         updated.
	 */
	@Modifying
	@Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.id IN :notificationsIds")
	public void markNotificationsAsRead(@Param("userId") long userId,
			@Param("notificationsIds") Set<Long> notificationsIds);

	/**
	 * Hides the given notification.
	 * 
	 * @param notificationId id of the notification to be hidden.
	 */
	@Modifying
	@Query("UPDATE Notification n SET n.hidden = true WHERE n.id = :notificationId")
	public void hideNotification(@Param("notificationId") long notificationId);

}
