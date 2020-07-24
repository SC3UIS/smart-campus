package com.uis.iot.admin.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.exception.RecordExistsException;
import com.uis.iot.common.model.AppUserPassDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Logic to handle the management of Users.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 * 
 */
public interface IUserService {

	/**
	 * Receives a user, with a <code>null</code> id, and creates it in the database.
	 * 
	 * @param user to be created.
	 * @return the created user with its assigned id.
	 * @throws RecordExistsException if the user name or email is already
	 *                               registered.
	 * @throws InternalException     if a persistence error occurred.
	 */
	public AppUser registerUser(final AppUser user);

	/**
	 * Receives a user, with an existing id, and updates it in the database.
	 * 
	 * @param user   to be updated.
	 * @param userId id of the user to be updated.
	 * @return the user with the updated info.
	 * @throws InvalidKeyException if the user doesn't exist.
	 * @throws InternalException   if a persistence error occurred.
	 * 
	 */
	public AppUser editUser(final AppUser user, final long userId);

	/**
	 * Validates that the credentials of a given user are correct and then returns
	 * the information about the user.
	 * 
	 * @param user to be verified.
	 * @return the user (if the credentials are correct).
	 * @throws InvalidKeyException if the credentials are wrong.
	 */
	public AppUser authenticateUser(final AppUser user);

	/**
	 * Receives a user id, and deletes it from the database (if exists).
	 * 
	 * @param userId id of the user to be deleted.
	 * @return message indicating whether the user was deleted.
	 * @throws InternalException if the user does not exist or the deletion of the
	 *                           user failed.
	 */
	public ResponseDTO deleteUser(final long userId);

	/**
	 * Retrieves the user identified by the given id.
	 * 
	 * @param userId id fo the user.
	 * @return the {@link AppUser}.
	 * @throw {@link InvalidKeyException} if the User doesn't exist.
	 */
	public AppUser getUser(final long userId);

	/**
	 * Get a list of all the users in the database.
	 * 
	 * @return a list of users, never <code>null</code>.
	 */
	public List<AppUser> getAllUsers();

	/**
	 * Receives a user id, and a password dto to change the user's password in the
	 * database.
	 * 
	 * @param passDTO dto containing the old password (for authentication) and the
	 *                new one.
	 * @param userId  id of the user who's requesting the password change.
	 * @return a {@link ResponseDTO} indicating that the operation went well.
	 * @throws InvalidKeyException     if the user doesn't exist.
	 * @throws BadCredentialsException if the old password is wrong.
	 * @throws InternalException       if a persistence error occurred.
	 */
	public ResponseDTO changePass(final AppUserPassDTO passDTO, final long userId);

	/**
	 * Sends the user an email with the new password to the given email address.
	 * 
	 * @param email address where the mail will be sent.
	 * @return a message to confirm a mail has been sent.
	 * @throws InvalidKeyException if the user doesn't exist.
	 * @throws InternalException   if the password couldn't be updated.
	 */
	public ResponseDTO retrievePass(final String email);

}