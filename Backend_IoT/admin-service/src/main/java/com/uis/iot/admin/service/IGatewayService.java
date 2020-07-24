package com.uis.iot.admin.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.entity.Notification;
import com.uis.iot.common.model.GatewayAssignmentDTO;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.ResponseDTO;
import com.uis.iot.common.model.TopicDTO;

public interface IGatewayService {

	public Gateway getGateway(long idGateway);

	/**
	 * Receives a dto of a gateway, with a null id, and creates it in the database.
	 * Throws exceptions for timeout or exceptions on the gateway side.
	 * 
	 * @param gatewayDTO dto of the gateway to be created
	 * @return dto of the created gateway with its assigned it
	 * 
	 */
	public GatewayDTO registerGateway(GatewayDTO gatewayDTO);

	/**
	 * Receives a dto of a gateway, and updates it in the database.
	 * Throws exceptions for timeout or exceptions on the gateway side.
	 * 
	 * @param gatewayDTO dto of the gateway to be created
	 * @return dto of the created gateway with its assigned it
	 */
	public GatewayDTO editGateway(GatewayDTO gatewayDTO, long idGateway);

	/**
	 * Deletes a gateway from the database.
	 * Throws exceptions for timeout or exceptions on the gateway side.
	 * 
	 * @param idGateway
	 * @return
	 */
	public ResponseDTO deleteGateway(long idGateway);

	/**
	 * Retrieves the Gateways that belong to an Application identified by its id.
	 * 
	 * @param idApplication application id to query the Gateways.
	 * @return the list of Gateways that belong to the given application, not
	 *         <code>null</code>.
	 */
	public List<Gateway> getGatewaysByApplicationId(long idApplication);

	/**
	 * Sends the keep alive message for a specific gateway, updating the Gateway and
	 * its Processes if an alive status changed, also sends, persists and returns a
	 * notification for each change to every user that owns the Gateway and the
	 * Administrators.
	 * 
	 * @param gatewayId id of the Gateway to send the keep alive message.
	 * @return a {@link MultiValueMap} that contains all the Notifications sent for
	 *         each user.
	 */
	public MultiValueMap<AppUser, Notification> sendKeepAlive(long gatewayId);

	/**
	 * Retrieves all Gateways
	 * 
	 * @return
	 */
	public List<Gateway> getAllGateways(boolean processes);

	/**
	 * Retrieves Gateways that have a Topic property whose value is topic.
	 * 
	 * @param idProcess
	 * @return
	 */
	public List<Gateway> getGatewaysByTopic(String topic);

	/**
	 * @param assignmentDTO
	 * @return
	 */
	public ResponseDTO assignToApp(GatewayAssignmentDTO assignmentDTO);

	/**
	 * @param gatewayId
	 * @return
	 */
	public List<TopicDTO> getAssociatedTopics(long gatewayId);

	/**
	 * Retrieves Gateways that have the process' id passed as a param.
	 * 
	 * @param processId Process' id
	 * @return List of Gateways that have the process' id passed as a param.
	 */
	public Gateway getGatewayByProcess(Long processId);

	/**
	 * Retrieves Gateways that have any the processes' ids passed as a param.
	 * 
	 * @param processIds Processes' id
	 * @return List of Gateways that have the processes' id passed as a param.
	 */
	public List<Gateway> getGatewaysByProcesses(List<Long> processId);

	/**
	 * Retrieves the Gateways that belong to a user identified by its id.
	 * 
	 * @param userId id of the user that owns the gateways.
	 * @return the list of gateways that belong to the given user, not
	 *         <code>null</code>.
	 */
	public List<Gateway> getGatewaysByUserId(final long userId);

}