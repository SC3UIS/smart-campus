package co.uis.iot.edge.core.vertx;

import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.ProcessAliveDTO;
import co.uis.iot.edge.common.model.ResponseDTO;
import io.vertx.core.Verticle;

/**
 * Defines the methods for Vert.x like TCP Event Bus Bridge communication and
 * Processes Verticles.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IVertxHandler extends Verticle {

	/**
	 * Publishes a message to the given topic in the TCP Event Bus bridge.
	 * 
	 * @param topic      the topic.
	 * @param messageDTO the message to be sent.
	 */
	public void publishToTopic(String topic, MessageDTO messageDTO);

	/**
	 * Deploys the JAR for the given Process.
	 * 
	 * @param processId  id of the Process.
	 * @param processJar PROCESS_JAR Property value.
	 * @param deploy     <code>true</code> to deploy the process, 
	 * 					 <code>false</code> to undeploy it.
	 * 
	 * @return a {@link ResponseDTO} object that indicates if the JAR was deployed successfully or not.
	 */
	public ResponseDTO deployProcess(Long processId, String processJar, boolean deploy);
	
	/**
	 * Sends a message to the given topic in the TCP Event Bus bridge and wait for the response to know if a process is working or not.
	 * 
	 * @param topic      the topic.
	 * @param message the message to be sent.
	 * @return the process with its status and message.
	 */
	public ProcessAliveDTO checkProcessAlive(String topic, String message);

}
