package com.uis.iot.data.service;

import java.util.Arrays;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.utils.CommonUtils;
import com.uis.iot.common.utils.ERoute;

/**
 * Implementation of AMQP Protocol. Used to communicate with the internal
 * broker.
 * 
 * @author Felipe Estupiñan
 * @author Kevin Arias
 *
 */
@Service
public class ActiveMQService implements IActiveMQService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQService.class);

	@Autowired
	private ApplicationContext context;

	@Value("${broker.url}")
	private String url;

	/**
	 * Subscribe to all the topics that are actually all the gateways' id
	 * 
	 * @param string
	 * @throws InterruptedException 
	 */
	public void subscribeAll() {
		LOGGER.info("Initiating subscription to queues.");
		for (int i = 0; i < 3; i++) {
			try {
				final List<GatewayDTO> gateways = Arrays.asList(CommonUtils
						.getHTTPResponse(ERoute.ADMIN + "/gateways", HttpMethod.GET, GatewayDTO[].class).getBody());
				for (final GatewayDTO gatewayDTO : gateways) {
					createConsumer(gatewayDTO.getId().toString());
					LOGGER.info("Subscription to the queue {} succeeded", gatewayDTO.getId());
				}
				break;
			} catch (final Exception e) {
				LOGGER.error("First subscription to the queues failed:", e);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String subscribe(String topic) {
		try {
			createConsumer(topic);
			return "Subscription to the queue " + topic + " succeeded";
		} catch (JMSException e) {
			LOGGER.error("Subscription to queue {} failed.", topic, e);
			return "Subscription to the queue " + topic + " failed";
		}
	}

	/**
	 * Creates a consumer for a given queue.
	 * 
	 * @throws JMSException if subscription to the queue failed.
	 * 
	 */
	private void createConsumer(final String queueName) throws JMSException {
		// Create a ConnectionFactory
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

		// Create a Connection
		final Connection connection = connectionFactory.createConnection();

		// Create a Session
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create the destination (Topic or Queue)
		final Destination destination = session.createQueue(queueName);

		// Create a MessageConsumer from the Session to the Topic or Queue
		final MessageConsumer consumer = session.createConsumer(destination);
		final MsgListener msgListener = context.getBean(MsgListener.class);

		consumer.setMessageListener(msgListener);
		connection.start();
	}
}
