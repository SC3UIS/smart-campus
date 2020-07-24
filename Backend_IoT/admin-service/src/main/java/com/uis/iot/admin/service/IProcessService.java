package com.uis.iot.admin.service;

import java.util.List;

import com.uis.iot.admin.entity.Process;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Logic to handle the management of Processes.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 */
public interface IProcessService {

	/**
	 * Finds a process by its id.
	 * 
	 * @param processId the id of the process.
	 * @return the found Process.
	 */
	public Process getProcess(final long processId);

	/**
	 * Retrieves the processes that belong to a gateway identified by its id.
	 * 
	 * @param gatewayId id of the Gateway to query the processes.
	 * @return the list of processes that belong to the given gateway, not
	 *         <code>null</code>.
	 */
	public List<Process> getProcessesByGateway(final long gatewayId);

	/**
	 * Retrieves the processes that belong to an Application identified by its id.
	 * 
	 * @param applicationId id of the Application to query the processes.
	 * @return the list of processes that belong to the given application, not
	 *         <code>null</code>.
	 */
	public List<Process> getProcessesByApplication(final long applicationId);

	/**
	 * Retrieves the processes that belong to an user identified by it's id.
	 * 
	 * @param userId - User id to query the processes.
	 * @return the list of processes that belong to the given user, not
	 *         <code>null</code>.
	 */
	public List<Process> getProcessesByUser(final long userId);

	/**
	 * Retrieves the processes that has a given topic in their properties.
	 * 
	 * @param topic topic to be found in the properties of the determined processes.
	 * @return the list of processes that has a property with the given topic, not
	 *         <code>null</code>.
	 */
	public List<Process> getProcessesByTopic(String topic);

	/**
	 * Returns the processes that match the param sent.
	 * 
	 * @param userId        id of the user which processes will be returned.
	 * @param applicationId id of the application which processes will be returned.
	 * @param topic         topic of the processes which will be returned.
	 * @param gatewayId     id of the gateway which processes will be returned.
	 * @param processId     id of the process which will be returned.
	 * @return a list of the process ids.
	 */
	public List<Long> getProcessIds(Long userId, Long applicationId, String topic, Long gatewayId, Long processId);

	/**
	 * Saves a new process in the database.
	 * 
	 * @param processDTO the process object to be registered as a {@link Process}.
	 * @return a {@link Process} that contains the process DTO, but with an assigned
	 *         id.
	 */
	public Process register(Process process);

	/**
	 * Updates a Process.
	 * 
	 * @param processDTO the process to be updated.
	 * @return the process with the updated info.
	 */
	public Process edit(Process processDTO);

	/**
	 * Delete the Process with the given id.
	 * 
	 * @param idProcess id of the process to be deleted
	 * @return message indicating whether the process was deleted or not.
	 */
	public ResponseDTO delete(long idProcess);

	/**
	 * Deploys the process (if the PROCESS_JAR property is configured) identified by
	 * its id.
	 * 
	 * @param processId id of the process to be deployed.
	 * @param deploy    <code>true</code> to deploy it, <code>false</code> to
	 *                  undeploy it.
	 * @return a {@link ResponseDTO} indicating if the operation succeded or not.
	 */
	public ResponseDTO deployProcess(long processId, boolean deploy);

}