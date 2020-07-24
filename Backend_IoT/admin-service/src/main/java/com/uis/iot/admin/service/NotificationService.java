package com.uis.iot.admin.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uis.iot.admin.entity.Notification;
import com.uis.iot.admin.repository.INotificationRepository;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;

@Service
public class NotificationService implements INotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	private INotificationRepository repository;

	@Override
	@Transactional
	public Notification createNotification(final Notification notification) {
		try {
			if (notification.getTimestamp() == null) {
				notification.setTimestamp(new Date());
			}
			return repository.save(notification);
		} catch (DataAccessException e) {
			LOGGER.error("La notificación no pudo ser creada", e);
			throw new InternalException("La notificación no pudo ser creada: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public Notification updateNotification(final Notification notification) {
		try {
			final Notification foundNotification = repository.findById(notification.getId())
					.orElseThrow(() -> new InvalidKeyException("La notificación no existe"));
			foundNotification.setAlive(notification.isAlive());
			foundNotification.setRead(notification.isRead());
			foundNotification.setTimestamp(notification.getTimestamp());
			foundNotification.setMessage(notification.getMessage());
			return repository.save(foundNotification);
		} catch (DataAccessException e) {
			LOGGER.error("La notificación no pudo ser actualizada", e);
			throw new InternalException("La notificación no pudo ser actualizada" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void markNotificationsAsRead(final Long userId, final Set<Long> notificationsIds) {
		try {
			repository.markNotificationsAsRead(userId, notificationsIds);
		} catch (DataAccessException e) {
			LOGGER.error("La notificaciones no fueron marcadas como leídas", e);
			throw new InternalException(
					"Las notificaciones no pudieron ser marcadas como leídas correctamente: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void hideNotification(final long notificationId) {
		try {
			repository.hideNotification(notificationId);
		} catch (DataAccessException e) {
			LOGGER.error("La notificación no pudo ser eliminada", e);
			throw new InternalException("La notificación no pudo ser eliminada correctamente: " + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public long getUnreadNotificationsCount(final Long userId) {
		return repository.countByUserIdAndReadFalseAndHiddenFalse(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Notification> getNotificationsByUserId(final Long userId, final boolean all, final Pageable page) {
		if (all) {
			return repository.findByUserId(userId, page);
		} else {
			return repository.findByUserIdAndHiddenFalse(userId, page);
		}
	}

}
