/**
 * 
 */
package com.uis.iot.data.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.gateway.model.MessageDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.TopicDTO;
import com.uis.iot.common.utils.CommonUtils;
import com.uis.iot.common.utils.ERoute;
import com.uis.iot.data.entity.Data;
import com.uis.iot.data.repository.IDataRepository;

/**
 * Class to manage the interactions with the incoming and outcoming IoT messages from the gateways. 
 * 
 * @author Felipe Estupiñan
 * @author Kevin Arias
 *
 */
@Component
@Scope("prototype")
public class MsgListener implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MsgListener.class);

	@Value("${broker.url}")
	private String url;

	@Autowired
	private IDataRepository repository;

	@Transactional
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			String text;
			try {
				text = textMessage.getText();
				List<MessageDTO> list = Arrays.asList(CommonUtils.mapFromJson(text, MessageDTO[].class));
				List<Data> listToSave = new ArrayList<>();
				for (MessageDTO gatewayDataDTO : list) {
					listToSave.add(Data.fromDTO(gatewayDataDTO));
				}
				
				ResponseEntity<GatewayDTO> response = CommonUtils.getHTTPResponse(ERoute.ADMIN + "/gateways/gateway/" + listToSave.get(0).getGatewayId(), HttpMethod.GET, GatewayDTO.class);
				if (response.getBody() instanceof GatewayDTO) {
					repository.saveAll(listToSave);
					
					TopicDTO[] topicsArray = CommonUtils.getHTTPResponse(
							ERoute.ADMIN + "gateways/gateway/" + listToSave.get(0).getGatewayId() + "/topics",
							HttpMethod.GET, TopicDTO[].class).getBody();

					List<TopicDTO> topics = new ArrayList<>();

					for (TopicDTO tdto : topicsArray) {
						topics.add(tdto);
					}
					
					// Create a ConnectionFactory
					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
					// Create a Connection
					Connection connection = connectionFactory.createConnection();
					connection.start();
					// Create a Session
					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					for (Data msg : listToSave) {
						String topic = topics.stream().filter(t -> t.getProcessId().equals(msg.getProcessId().toString()))
								.findFirst().get().getValue();
						postToTopic(topic, msg.toDTO(), connection, session);
					}
					
					// Clean up
					session.close();
					connection.close();

					System.out.println("Received: " + text);
				}
				else {
					LOGGER.error("El gateway no existe en el sistema");
					throw new InternalException("El gateway no existe en el sistema");
				}
				
			} catch (JMSException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Received: " + message);
		}
	}

	/**
	 * @param topic
	 * @param dto
	 * @param session 
	 * @param connection 
	 * @throws JMSException 
	 * @throws JsonProcessingException 
	 */
	private void postToTopic(String topic, MessageDTO dto, Connection connection, Session session) throws JMSException, JsonProcessingException {
		// Create the destination (Topic or Queue)
		Destination destination = session.createTopic(topic);

		// Create a MessageProducer from the Session to the Topic or Queue
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// Create a message
		TextMessage message = session.createTextMessage(CommonUtils.mapToJson(dto));

		// Tell the producer to send the message
		System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
		producer.send(message);
	}
}
