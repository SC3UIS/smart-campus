package com.uis.iot.admin.repository;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.uis.iot.admin.entity.AppUser;

/**
 * Executes database operations involving the {@link AppUser} entity.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 * 
 */
@Repository
public interface IUserRepository extends CrudRepository<AppUser, Long> {

	/**
	 * Finds a user by its email.
	 * 
	 * @param email of the User.
	 * @return an {@link Optional} wrapping the user.
	 */
	public Optional<AppUser> findByEmail(String email);

	/**
	 * Finds a user by its username.
	 * 
	 * @param username of the User.
	 * @return an {@link Option} wrapping the user.
	 */
	public Optional<AppUser> findByUsername(String username);

	/**
	 * Retrieves all users that own the given gateway identified by its id.
	 * 
	 * @param gatewayId id of the gateway.
	 * @return the users that have at least one application with the given gateway.
	 *         Never <code>null</code> and doesn't include Administrators.
	 */
	public Set<AppUser> findByGatewaysId(Long gatewayId);

	/**
	 * Retrieves all administrator users.
	 * 
	 * @return a Set of admins, never <code>null</code>.
	 */
	public Set<AppUser> findByAdminTrue();

}
