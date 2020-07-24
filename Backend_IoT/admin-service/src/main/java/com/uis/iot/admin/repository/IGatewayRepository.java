package com.uis.iot.admin.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uis.iot.admin.entity.Gateway;

/**
 * Executes database operations involving the {@link Gateway} entity.
 * 
 * @author Kevin
 *
 */
@Repository
public interface IGatewayRepository extends PagingAndSortingRepository<Gateway, Long> {

	/**
	 * Finds the gateways that belong to the given application with a specified
	 * Sort.
	 * 
	 * @param applicationId id of the application.
	 * @param sort          {@link Sort} to execute any sorting.
	 * @return application's gateways. Never <code>null</code>.
	 */
	public List<Gateway> findByApplicationsId(long applicationId, Sort sort);

	/**
	 * Updates the alive status for a given gateway identified by its id.
	 * 
	 * @param gatewayId id of the Gateway to be updated.
	 * @param alive     <code>true</code> if the gateway is alive,
	 *                  <code>false</code> otherwise.
	 */
	@Modifying
	@Query("UPDATE Gateway g SET g.alive = :isAlive WHERE g.id = :gatewayId")
	public void updateGatewayAliveStatus(@Param("gatewayId") long gatewayId, @Param("isAlive") boolean alive);

	/**
	 * Gets a list of all the processes of the gateway and their corresponding
	 * topics.
	 * 
	 * @param gatewayId id of the Gateway.
	 */
	@Query(value = "SELECT id_process, value FROM process_property AS PP WHERE id_process IN (SELECT id_process AS P FROM process WHERE id_gateway = :gatewayId) AND PP.name = 'topic'", nativeQuery = true)
	public List<Object[]> getAssociatedTopics(@Param("gatewayId") long gatewayId);

	/**
	 * Finds the gateways to which the given process belongs.
	 * 
	 * @param processId id of the process.
	 * 
	 */
	@Query("SELECT p.gateway FROM Process p WHERE p.id = :processId")
	public Gateway findByProcessId(@Param("processId") long processId);

	/**
	 * Finds the gateways to which any of the given processes belong.
	 * 
	 * @param processesId ids of the process.
	 */
	@Modifying
	@Query("select gat from Gateway gat where gat.id in (select pro.gatewayId from Process pro where pro.id in (:processIds))")
	public List<Gateway> findByProcessIds(@Param("processIds") List<Long> processIds);

	/**
	 * Finds the gateways that belong to the given user with a specified Sort.
	 * 
	 * @param userId id of the user.
	 * @param sort   {@link Sort} to be executed.
	 * @return user's gateways. Never <code>null</code>.
	 */
	public List<Gateway> findByUserId(long userId, Sort sort);

	/**
	 * Retrieves the amount of alive gateways for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of alive gateways.
	 */
	public long countByUserIdAndAliveTrue(long userId);

	/**
	 * Retrieves the amount of death gateways for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of death gateways.
	 */
	public long countByUserIdAndAliveFalse(long userId);

}
