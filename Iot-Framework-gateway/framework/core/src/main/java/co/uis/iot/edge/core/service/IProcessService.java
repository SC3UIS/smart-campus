package co.uis.iot.edge.core.service;

import java.util.List;

import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.ResponseDTO;
import co.uis.iot.edge.core.exception.MissingTopicException;
import co.uis.iot.edge.core.exception.PersistenceException;
import co.uis.iot.edge.core.exception.ProcessAlredyExistException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;

/**
 * Processes business layer.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IProcessService {

	/**
	 * Obtains the list of all Processes.
	 * 
	 * @return the existing Processes as DTOs.
	 */
	public List<ProcessDTO> getProcesses();

	/**
	 * Obtains the Process with the given id in the Backend solution.
	 * 
	 * @param idBackend the Id of the Process in the backend.
	 * @return the equivalent {@link ProcessDTO} of the found Process, or an
	 *         exception if it doesn't exist
	 */
	public ProcessDTO getProcessByIdBackend(Long idBackend);

	/**
	 * Obtains the Process with the given deployed status.
	 * 
	 * @param deployed <code>true</code> to query the deployed jars,
	 *                 <code>false</code> otherwise.
	 * @return the matching Processes as DTO.
	 */
	public List<ProcessDTO> getProcessByDeployedStatus(boolean deployed);

	/**
	 * Creates the given Process.
	 * 
	 * @param process to be inserted.
	 * @return the equivalent {@link ProcessDTO} of the created Process, or
	 *         <code>null</code> if the operation failed.
	 * @throws PersistenceException        if the operation was not successful.
	 * @throws ProcessAlredyExistException if the process already exists.
	 * @throws MissingTopicException       if the Topic property is not included.
	 */
	public ProcessDTO createProcess(ProcessDTO processDTO);

	/**
	 * Updated the given Process.
	 * 
	 * @param process to be updated.
	 * @return the Process updated or <code>null</code> if the operation failed.
	 * @throws PersistenceException     if the operation was not successful.
	 * @throws ProcessNotFoundException if the process doesn't exist.
	 * @throws IllegalArgumentException if the Topic property is not configured
	 *                                  correctly.
	 */
	public ProcessDTO updateProcess(ProcessDTO processDTO);

	/**
	 * Updates the {@link Process} deployed status.
	 * 
	 * @param processId id of the process to be updated.
	 * @param deployed  <code>true</code> to set the {@link Process} as deployed,
	 *                  <code>false</code> otherwise
	 * @throws ProcessNotFoundException if the process doesn't exist.
	 * @return the modified Process as DTO or <code>null</code> if the update
	 *         failed.
	 */
	public ProcessDTO updateProcessDeployStatus(Long processId, boolean deployed);

	/**
	 * Deletes the given Process identified by it's id. If the operation failed an
	 * exception is thrown.
	 * 
	 * @param idBackend the id in the backend of the Process to be deleted.
	 * @throws PersistenceException     if the operation was not successful.
	 * @throws ProcessNotFoundException if the process doesn't exist.
	 */
	public void deleteProcess(Long idBackend);
	
	/**
	 * Deploys/undeploys the given process identified by its id.
	 * 
	 * @param processId id of the Process to be deployed.
	 * @param deploy    <code>true</code> to deploy the Process, <code>false</code>
	 *                  to undeploy it.
	 * @return a {@link ResponseDTO} object that indicates if the JAR was deployed successfully or not.
	 */
	public ResponseDTO deployProcess(long processId, boolean deploy);
	/**
	 * Used to initialize all batch frequency task schedulers on Startup.
	 */
	public void initBatchFrequencyTasks();

	/**
	 * Initializes the Deployed Processes with a proper PROCESS_JAR property
	 * configuration.
	 */
	public void initDeployedProcesses();

}
