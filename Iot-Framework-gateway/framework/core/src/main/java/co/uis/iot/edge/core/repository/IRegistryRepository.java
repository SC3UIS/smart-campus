package co.uis.iot.edge.core.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.core.persistence.Registry;

/**
 * Repository used for operations over {@link Registry} entity.
 * 
 * @author Camilo Gutiérrez.
 *
 */
public interface IRegistryRepository extends MongoRepository<Registry, Long> {

	/**
	 * Find a device (of a given type) by it's backend id.
	 * 
	 * @param idBackend the id sent from the backend.
	 * @param type      of the device.
	 * @return the requested device if exists.
	 */
	public Optional<Registry> findByIdBackendAndType(Long idBackend, EThingType type);

	/**
	 * Find a device by its type (gateway, sensor, actuator).
	 * 
	 * @param type indicates the decive's type ex: GATEWAY
	 * @return the requested device if exists.
	 */
	public List<Registry> findByType(EThingType type);

	/**
	 * Finds all the devices that match any of the passed ids.
	 * 
	 * @param backendIds to be evaluated
	 * @return a {@link List} of Devices mapped as a {@link Registry}.
	 */
	public List<Registry> findByIdBackendIn(Collection<Long> backendIds);

	/**
	 * Deletes a single {@link Registry} with the given idBackend and type.
	 * 
	 * @param idBackend the id sent from the backend.
	 * @param type      of the device.
	 * @return the amount of deleted devices.
	 */
	public Long deleteByIdBackendAndType(Long idBackend, EThingType type);

	/**
	 * Delete all the devices that match with the passed type. For instance if the
	 * type is SENSOR every saved sensor will be deleted.
	 * 
	 * @param type device's type.
	 * @return the deleted devices.
	 */
	public List<Registry> deleteByType(EThingType type);

	/**
	 * Delete all the devices that match with the passed list of backend's ids.
	 * 
	 * @param backendIds list of ids to be deleted.
	 * @param type       devices' type.
	 * @return the list with the removed devices.
	 */
	public List<Registry> deleteByIdBackendInAndType(List<Long> backendIds, EThingType type);
}
