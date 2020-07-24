package com.uis.iot.admin.service;

import java.util.List;

import com.uis.iot.admin.entity.Application;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Logic to handle the management of Applications.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
public interface IApplicationService {

	/**
	 * Registers a new application in the database.
	 * 
	 * @param application The application to be registered.
	 * 
	 * @return the application created.
	 * 
	 * @throws InvalidKeyException if the user name does not exist.
	 * @throws InternalException   if an error occurred creating the application.
	 * 
	 */
	public Application registerApplication(final Application application);

	/**
	 * Updates an existing application.
	 * 
	 * @param application The application object to be updated.
	 * @return the application updated.
	 * @throws InvalidKeyException if the user doesn't exist.
	 * @throws InternalException   if any persistence error occurred.
	 */
	public Application editApplication(final Application application);

	/**
	 * Deletes an application if exists.
	 * 
	 * @param appId The id of the application to be deleted.
	 * @return a {@link ResponseDTO} that indicates if the deletion was successful
	 *         or not.
	 * @throws InvalidKeyException if the application doesn't exist.
	 * @throws InternalException   if any persistence error occurred.
	 */
	public ResponseDTO deleteApplication(final Long appId);

	/**
	 * Retrieves the Applications that belong to a user identified by its id.
	 * 
	 * @param userId id of the user that owns the applications.
	 * @return the list of Applications that belong to the given user, not
	 *         <code>null</code>.
	 * @throws InvalidKeyException if the user doesn't exist.
	 */
	public List<Application> getApplicationsByUserId(final long userId);

	/**
	 * Retrieves an Application by it's id.
	 * 
	 * @param appId Application id.
	 * @return the found Application
	 * @throws InvalidKeyException if the user doesn't exist.
	 */
	public Application getApplicationById(final long appId);

	/**
	 * Assigns or unassigns a gateway identified by its id to an application.
	 * 
	 * @param applicationId id of the application.
	 * @param gatewayId     id of the gateway.
	 * @param assign        <code>true</code> to assign the gateway,
	 *                      <code>false</code> to unassign it.
	 */
	public void assignGatewayToApplication(long applicationId, long gatewayId, boolean assign);
}
