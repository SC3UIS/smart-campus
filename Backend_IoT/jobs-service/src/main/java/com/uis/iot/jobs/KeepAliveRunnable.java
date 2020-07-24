package com.uis.iot.jobs;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.model.AppUserDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.NotificationDTO;
import com.uis.iot.common.model.ProcessDTO;
import com.uis.iot.common.model.StatisticsDTO;
import com.uis.iot.common.model.StatusChangeDTO;
import com.uis.iot.common.model.UserNotificationsDTO;
import com.uis.iot.common.utils.CommonUtils;
import com.uis.iot.common.utils.ERoute;

/**
 * Sends a keep alive message to all gateways (to also obtain the information
 * about their processes) to verify if the gateways & processes are available or
 * not. For any "alive" status change creates and sends a notification to the
 * user (owner and admin(s)) and at the end of the job it sends an email to
 * notify all status changes if any.
 * 
 * @author Federico
 *
 */
@Component
@Scope("prototype")
public class KeepAliveRunnable implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveRunnable.class);

	private Map<Long, ProcessDTO> processesMap = new HashMap<>();

	private Map<Long, String> gatewayMessageBodiesById = new HashMap<>();

	@Autowired
	private ThreadPoolTaskScheduler threadPoolScheduler;

	@Autowired
	private MessageSender messageSender;

	public KeepAliveRunnable() {
		super();
	}

	@Override
	public void run() {
		final Instant startDate = Instant.now();
		long emailsSend = 0;
		long emailStartMilis = 0;
		LOGGER.info("Keep alive process started.");
		final List<Callable<Object>> tasks = new LinkedList<>();
		try {
			final ResponseEntity<GatewayDTO[]> response = CommonUtils
					.getHTTPResponse(ERoute.ADMIN + "gateways?processes=true", HttpMethod.GET, GatewayDTO[].class);

			final List<GatewayDTO> gateways = Arrays.asList(((GatewayDTO[]) response.getBody()));
			Assert.isTrue(!CollectionUtils.isEmpty(gateways), "No gateways registered, skipping Keep Alive process.");

			final MultiValueMap<AppUserDTO, MultiValueMap<GatewayDTO, NotificationDTO>> notificationsByUserAndGateway = new LinkedMultiValueMap<>();

			for (GatewayDTO gateway : gateways) {
				LOGGER.info("Sending Keep alive to Gateway {}", gateway.getId());
				gateway.getProcesses().forEach(process -> processesMap.put(process.getId(), process));
				tasks.add(() -> {
					sendKeepAlive(gateway, notificationsByUserAndGateway);
					return null;
				});
			}

			// Send Keep Alive request in parallel.
			executeTasks(tasks);
			tasks.clear();

			// Send mails and updates to every user.
			emailStartMilis = Instant.now().toEpochMilli();
			for (Entry<AppUserDTO, List<MultiValueMap<GatewayDTO, NotificationDTO>>> notificationsByUserAndGatewayEntry : notificationsByUserAndGateway
					.entrySet()) {
				AppUserDTO user = notificationsByUserAndGatewayEntry.getKey();
				List<MultiValueMap<GatewayDTO, NotificationDTO>> notificationsByGateway = notificationsByUserAndGatewayEntry
						.getValue();
				tasks.add(() -> {
					sendEmailAndNotificationsToUser(user, notificationsByGateway, startDate);
					return null;
				});
				tasks.add(() -> {
					sendUpdatesToUser(user.getId(), notificationsByGateway, startDate);
					return null;
				});
			}
			emailsSend = tasks.size();

			// Send the mails and updates in parallel.
			executeTasks(tasks);

		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ResourceAccessException e) {
			LOGGER.error("Couldn't retrieve the gateways and processes, the administration service is not available.",
					e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("Couldn't retrieve the gateways and processes, an error occurred.", e);
		} catch (Exception e) {
			LOGGER.error("An error occurred in keep alive process.", e);
		} finally {
			LOGGER.info(
					"Keep alive Process finished. Took: {} ms. Emails send: {}. Time sending emails and alerts: {} ms",
					Instant.now().toEpochMilli() - startDate.toEpochMilli(), emailsSend,
					(Instant.now().toEpochMilli() - emailStartMilis));
			processesMap.clear();
			gatewayMessageBodiesById.clear();
		}
	}

	/**
	 * Executes the given tasks in parallel.
	 * 
	 * @param tasks a {@link List} (non <code>null</code>) of {@link Callable}
	 *              tasks.
	 */
	private void executeTasks(final List<Callable<Object>> tasks) {
		try {
			final ExecutorServiceAdapter executorService = new ExecutorServiceAdapter(threadPoolScheduler);
			executorService.invokeAll(tasks);
		} catch (InterruptedException e) {
			LOGGER.error("Keep alive process was interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private void sendEmailAndNotificationsToUser(final AppUserDTO user,
			final List<MultiValueMap<GatewayDTO, NotificationDTO>> notificationsByGateway, final Instant startDate) {
		final StringBuilder userMessageBody = new StringBuilder();
		List<NotificationDTO> notifications;
		for (MultiValueMap<GatewayDTO, NotificationDTO> notificationsByGatewayMap : notificationsByGateway) {
			for (Entry<GatewayDTO, List<NotificationDTO>> notificationsByGatewayEntry : notificationsByGatewayMap
					.entrySet()) {
				notifications = notificationsByGatewayEntry.getValue();
				sendNotifications(user.getId(), notifications);
				getGatewayEmailBody(notificationsByGatewayEntry.getKey(), notifications, userMessageBody);

			}
		}
		sendKeepAliveEmailToUser(user, userMessageBody.toString(), startDate);
	}

	private void sendUpdatesToUser(final long userId,
			final List<MultiValueMap<GatewayDTO, NotificationDTO>> notificationsByGateway,
			final Instant executionDate) {
		long gatewaysAlive = 0;
		long gatewaysDeath = 0;
		long processesAlive = 0;
		long processesDeath = 0;
		long alive = 0;
		long death = 0;
		for (MultiValueMap<GatewayDTO, NotificationDTO> notificationsByGatewayMap : notificationsByGateway) {
			for (Entry<GatewayDTO, List<NotificationDTO>> notificationsByGatewayEntry : notificationsByGatewayMap
					.entrySet()) {
				for (NotificationDTO notification : notificationsByGatewayEntry.getValue()) {
					if (notification.isAlive()) {
						if (notification.getProcessId() == null) {
							gatewaysDeath--;
							gatewaysAlive++;
						} else {
							processesDeath--;
							processesAlive++;
						}
						alive++;
					} else {
						if (notification.getProcessId() == null) {
							gatewaysAlive--;
							gatewaysDeath++;
						} else {
							processesAlive--;
							processesDeath++;
						}
						death++;
					}
				}

			}
		}
		final StatisticsDTO stats = new StatisticsDTO();
		stats.setGatewaysAlive(gatewaysAlive);
		stats.setGatewaysDeath(gatewaysDeath);
		stats.setProcessesAlive(processesAlive);
		stats.setProcessesDeath(processesDeath);
		stats.setChanges(Collections.singletonList(new StatusChangeDTO(Date.from(executionDate), alive, death)));
		try {
			messageSender.sendUpdate(userId, stats);
		} catch (JsonProcessingException | JMSException e) {
			LOGGER.error("Couldn't send the update for the user {}.", userId, e);
		}
	}

	private void sendNotifications(long userId, final List<NotificationDTO> notifications) {
		for (NotificationDTO notification : notifications) {
			try {
				messageSender.sendNotification(userId, notification);
			} catch (JsonProcessingException | JMSException e) {
				LOGGER.error("Couldn't send a notification for the user {}.", userId, e);
			}
		}
	}

	private void getGatewayEmailBody(final GatewayDTO gateway, final List<NotificationDTO> notifications,
			final StringBuilder userMessageBody) {
		String gatewayMessageBody;
		if (gatewayMessageBodiesById.containsKey(gateway.getId())) {
			gatewayMessageBody = gatewayMessageBodiesById.get(gateway.getId());
		} else {
			gatewayMessageBody = buildEmailGatewayMessage(gateway.getName(), notifications);
			gatewayMessageBodiesById.put(gateway.getId(), gatewayMessageBody);
		}
		userMessageBody.append(gatewayMessageBody);
	}

	/**
	 * Sends a Keep Alive email to a specific user.
	 * 
	 * @param user      that will receive the email.
	 * @param emailBody body of the email.
	 * @param startDate {@link Instant} when the job started.
	 */
	private void sendKeepAliveEmailToUser(AppUserDTO user, String emailBody, final Instant startDate) {
		try {
			LOGGER.info("Sending email to user {}", user.getId());
			CommonUtils.sendEmail(user.getEmail(), "Actualización estado de dispositivos - SMART CAMPUS",
					buildEmailBodyStructure(user.getUsername(), startDate.atZone(ZoneId.systemDefault()), emailBody));
		} catch (InternalException e) {
			LOGGER.error("An error occurred sending the email for the user {}", user.getId(), e);
		}
	}

	/**
	 * Consumes the Keep Alive REST Service of a specific Gateway and appends the
	 * returned notifications to a {@link MultiValueMap} of Notifications by User
	 * and Gateway to be later used to send the email for every interested user.
	 * 
	 * @param gateway                       to send the keep alive.
	 * @param notificationsByUserAndGateway {@link MultiValueMap} of Notifications
	 *                                      by User and Gateway. Non
	 *                                      <code>null</code>.
	 */
	private void sendKeepAlive(GatewayDTO gateway,
			final MultiValueMap<AppUserDTO, MultiValueMap<GatewayDTO, NotificationDTO>> notificationsByUserAndGateway) {
		MultiValueMap<GatewayDTO, NotificationDTO> notificationsByGateway;
		try {
			final ResponseEntity<UserNotificationsDTO[]> response = CommonUtils.getHTTPResponse(
					String.format("%sgateways/gateway/%d/keepAlive", ERoute.ADMIN, gateway.getId()), HttpMethod.GET,
					UserNotificationsDTO[].class);
			for (UserNotificationsDTO userNotifications : Arrays.asList((UserNotificationsDTO[]) response.getBody())) {
				notificationsByGateway = new LinkedMultiValueMap<>();
				notificationsByGateway.addAll(gateway, userNotifications.getNotifications());
				notificationsByUserAndGateway.add(userNotifications.getUser(), notificationsByGateway);
			}
		} catch (ResourceAccessException e) {
			LOGGER.error(
					"Couldn't send the keep alive request to the gateway {}, the administration service is not available.",
					gateway.getId(), e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("Couldn't send the keep alive request to the gateway {}, an error occurred.", gateway.getId(),
					e);
		} catch (Exception e) {
			LOGGER.error("An error occurred sending keep alive to gateway {}", gateway.getId(), e);
		}
	}

	private String buildEmailBodyStructure(String userName, ZonedDateTime messageDate, String body) {
		final StringBuilder builder = new StringBuilder(256).append("<html>").append("<head><style>").append(
				".gateway-table { border: solid 1px #DDEEEE; border-collapse: collapse;  border-spacing: 0; font: normal 13px Arial, sans-serif; }")
				.append(".gateway-table thead th { background-color: #DDEFEF; border: solid 1px #DDEEEE; color: #336B6B; padding: 10px; text-align: center; text-shadow: 1px 1px 1px #fff; }")
				.append(".gateway-table tbody td { border: solid 1px #DDEEEE; color: #333; padding: 10px; text-shadow: 1px 1px 1px #fff; }")
				.append("</style></head>").append("<body style='margin: 20px; font: normal 14px Arial, sans-serif;>")
				.append("<h1 style='text-align: center; color: #20aee3; font-weight: 300; margin-bottom: 0;'>SMART CAMPUS</h1>")
				.append("<h3 style='text-align: center'>Actualización de estado de dispositivos</h3>")
				.append("<p style='margin-top: 35px'>Estimado %s, el estado de algunos de sus dispositivos ha cambiado el %s a las %s.</p>")
				.append("<p style='margin-bottom: 35px'>A continuación verá un breve resumen de los cambios que se presentaron:</p>")
				.append("%s").append("</body>").append("</html>");
		return String.format(builder.toString(), userName,
				DateTimeFormatter.ofPattern("EEEE dd MMMM", new Locale("es")).format(messageDate),
				DateTimeFormatter.ofPattern("hh:mm a z").format(messageDate), body);
	}

	private String buildEmailGatewayMessage(String gatewayName, List<NotificationDTO> notifications) {
		final List<NotificationDTO> notificationsCopy = new ArrayList<>(notifications);
		final NotificationDTO gatewayNotification = notificationsCopy.stream()
				.filter(notification -> notification.getProcessId() == null).findFirst().orElse(null);
		final StringBuilder builder = new StringBuilder()
				.append("<span style='font-size: 16px; font-weight: bolder; margin-bottom: 0'>%s</span>");
		if (gatewayNotification != null) {
			notificationsCopy.remove(gatewayNotification);
			builder.append("<br/><span style='font-size: 13px'>").append("Cambió: <i>")
					.append(gatewayNotification.isAlive() ? "Activo" : "Inactivo").append(".</i><br/>")
					.append(gatewayNotification.getMessage()).append("</span>");
		}
		for (NotificationDTO notification : notificationsCopy) {
			builder.append("<br/><br/>").append("<table class='gateway-table'>").append("<thead>")
					.append("<th>Proceso</th>").append("<th>Estado</th>").append("<th>Información</th>")
					.append("</thead>").append("<tbody>");
			if (notification.getProcessId() != null) {
				builder.append("<tr><td>").append(processesMap.get(notification.getProcessId()).getName())
						.append("</td>").append("<td>").append(notification.isAlive() ? "Activo" : "Inactivo")
						.append("</td>").append("<td>").append(notification.getMessage()).append("</td></tr>");
			}
		}
		builder.append("</tbody>").append("</table>");

		return String.format(builder.toString(), gatewayName);
	}

}
