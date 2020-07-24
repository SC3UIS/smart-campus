/**
 * 
 */
package co.uis.iot.edge.core.service;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.uis.iot.edge.core.exception.CommunicationException;

/**
 * @author Camilo Gutiérrez
 *
 */
@Service
public class ActiveMQService implements IActiveMQService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQService.class);

	private Connection connection;
	private Session session;

	@Override
	public void connectToBroker(String brokerUrl) {
		try {
			disconnectFromBroker();

			// Create a Connection
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
			connection = connectionFactory.createConnection();
			connection.start();

			// Create a session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			LOGGER.info("Connection established with the broker:" + brokerUrl);

		} catch (JMSException e) {
			LOGGER.error("There were a problem trying to connect to the broker: {}", brokerUrl, e);
		}
	}

	@Override
	public void sendMessageToQueue(String queueName, String message) throws CommunicationException {

		if (session == null) {
			throw new CommunicationException("There's not an active connection to the broker.");
		}

		try {
			Destination destination = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(destination);

			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
		} catch (JMSException e) {
			LOGGER.error("There were a problem sending the message to the queue: {}", queueName, e);
			throw new CommunicationException("There were a problem sending the message to the queue: " + queueName);
		}
	}

	@Override
	public void disconnectFromBroker() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (JMSException e) {
			LOGGER.error("There were a problem disconnecting from broker.", e);
		}
	}

}
