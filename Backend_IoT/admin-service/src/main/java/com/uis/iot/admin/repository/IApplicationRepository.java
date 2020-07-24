package com.uis.iot.admin.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.uis.iot.admin.entity.Application;

/**
 * Executes database operations involving the {@link Application} entity.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@Repository
public interface IApplicationRepository extends PagingAndSortingRepository<Application, Long> {

	/**
	 * Finds the applications that belong to the given user with a specified Sort.
	 * 
	 * @param userId id of the user.
	 * @param sort   {@link Sort} to be executed.
	 * @return user's applications. Never <code>null</code>.
	 */
	public List<Application> findByUserId(long userId, Sort sort);

	/**
	 * Retrieves the amount of Applications for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of applications.
	 */
	public long countByUserId(long userId);

}
