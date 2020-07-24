package com.uis.iot.admin.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.uis.iot.admin.entity.Process;

/**
 * Executes database operations involving the {@link Process} entity.
 * 
 * @author felipe.estupinan
 * @author Federico
 *
 */
public interface IProcessRepository extends PagingAndSortingRepository<Process, Long> {

	/**
	 * Finds all the Processes that belongs to the given Gateway identified by its
	 * id.
	 * 
	 * @param gatewayId id of the Gateway.
	 * @param           {@link Sort} to be executed.
	 * @return a {@link List} of Processes. Never <code>null</code>.
	 */
	public List<Process> findByGatewayId(long gatewayId, Sort sort);

	/**
	 * Finds all the Processes that belongs to the Given Application identified by
	 * its id.
	 * 
	 * @param applicationId id of the Application.
	 * @param               {@link Sort} to be executed.
	 * @return a {@link List} of Processes. Never <code>null</code>.
	 */
	public List<Process> findByGatewayApplicationsId(long applicationId, Sort sort);

	/**
	 * Finds all the Processes that belongs to the Given User identified by its id.
	 * 
	 * @param userId id of the User.
	 * @param        {@link Sort} to be executed.
	 * @return a {@link List} of Processes. Never <code>null</code>.
	 */
	public List<Process> findByGatewayUserId(long userId, Sort sort);

	/**
	 * Retrieves the amount of processes alive for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of processes alive.
	 */
	public long countByGatewayUserIdAndAliveTrue(long userId);

	/**
	 * Retrieves the amount of processes alive for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of processes alive.
	 */
	public long countByGatewayUserIdAndAliveFalse(long userId);

	/**
	 * Finds all the Processes with a given topic.
	 * 
	 * @param topic property topic of the processes to be found.
	 * @return a {@link List} of Processes. Never <code>null</code>.
	 */
	public List<Process> findByProcessPropertiesIdNameAndProcessPropertiesValue(String name, String value);

	/**
	 * Updates the alive status of a Process identified by its id.
	 * 
	 * @param processId id of the Process to be updated.
	 * @param alive     <code>true</code> if the Process is alive, false otherwise.
	 */
	@Modifying
	@Query("UPDATE Process p SET p.alive = :isAlive WHERE p.id = :processId")
	public void updateProcessAliveStatus(@Param("processId") long processId, @Param("isAlive") boolean alive);

}
