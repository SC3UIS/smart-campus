/**
 * 
 */
package co.uis.iot.edge.core.service;

import co.uis.iot.edge.core.exception.CommunicationException;

/**
 * Service to connect and send data to a broker through ActiveMQ
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IActiveMQService {
	
	/**
	 * Connects to a broker by its url and to an specific queue by its name.
	 * 
	 * @param brokerUrl - Link to connect to the broker.
	 */
	public void connectToBroker(String brokerUrl);
	
	
	/**
	 * Sends an object message to a queue in the broker.
	 * 
	 * @param queueName - Queue's name
	 * @param message - Message to be sent.
	 * @throws CommunicationException if there's an error sending the message.
	 */
	public void sendMessageToQueue(String queueName, String message) throws CommunicationException;
	
	
	/**
	 * Close the current connection to the broker.
	 */
	public void disconnectFromBroker();
}
