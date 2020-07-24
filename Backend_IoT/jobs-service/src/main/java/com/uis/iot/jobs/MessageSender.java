package com.uis.iot.jobs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uis.iot.common.model.NotificationDTO;
import com.uis.iot.common.model.StatisticsDTO;

/**
 * Notification and update sender using ActiveMQ.
 * 
 * @author Felipe Estupiñan
 * @author Kevin Arias
 * @author Federico Camacho
 *
 */
@Service
public class MessageSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

	@Value("${broker.url}")
	private String url;

	private Session session;

	private Connection connection;

	private final Map<Long, MessageProducer> notificationProducers = new HashMap<>();

	private final Map<Long, MessageProducer> updateProducers = new HashMap<>();

	private final ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	public void initAfterStartup() {
		try {
			connect();
		} catch (JMSException e) {
			LOGGER.error("Couldn't start ActiveMQ connection, Notifications won't be send.", e);
		}
	}

	@PreDestroy
	public void cleanupBeforeExit() {
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			LOGGER.error("An error occurred disconnecting from ActiveMQ", e);
		}
	}

	private void connect() throws JMSException {
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		connection = connectionFactory.createConnection();
		connection.setClientID("JobService");
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	private MessageProducer getNotificationProducer(long userId) throws JMSException {
		if (session == null) {
			connect();
		}

		if (notificationProducers.containsKey(userId)) {
			return notificationProducers.get(userId);
		}

		final MessageProducer producer = session.createProducer(session.createTopic("notifications-" + userId));
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		notificationProducers.put(userId, producer);
		return producer;
	}

	private MessageProducer getUpdateProducer(long userId) throws JMSException {
		if (session == null) {
			connect();
		}

		if (updateProducers.containsKey(userId)) {
			return updateProducers.get(userId);
		}

		final MessageProducer producer = session.createProducer(session.createTopic("updates-" + userId));
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		updateProducers.put(userId, producer);
		return producer;
	}

	/**
	 * Sends a notification to the given user.
	 * 
	 * @param userId       id of the user.
	 * @param notification {@link NotificationDTO} to be sent.
	 * @throws JMSException            if an error occurred sending the message.
	 * @throws JsonProcessingException if the message is malformed.
	 */
	public void sendNotification(long userId, NotificationDTO notification)
			throws JMSException, JsonProcessingException {
		final TextMessage message = session.createTextMessage(mapper.writeValueAsString(notification));
		getNotificationProducer(userId).send(message);

	}

	/**
	 * Sends an update to the given user.
	 * 
	 * @param userId    id of the user.
	 * @param statistic the {@link StatisticsDTO} to be sent.
	 * @throws JMSException            if an error occurred sending the message.
	 * @throws JsonProcessingException if the message is malformed.
	 */
	public void sendUpdate(long userId, StatisticsDTO statistic) throws JMSException, JsonProcessingException {
		final TextMessage message = session.createTextMessage(mapper.writeValueAsString(statistic));
		getUpdateProducer(userId).send(message);

	}

}
