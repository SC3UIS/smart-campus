package co.uis.iot.edge.core.service;

import java.util.List;

import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.core.persistence.Registry;

/**
 * Domain logic to handle registration of Things {@link EThingType} in the
 * Gateway.
 * 
 * @author Camilo Gutiérrez
 *
 */
public interface IRegistryService {

	/**
	 * Obtains the list of {@link Registry} that match the given type or all the
	 * records if the type is <code>null</code>.
	 * 
	 * @param type of the Registries.
	 * @return the found Registries that match the given condition.
	 */
	public List<RegistryDTO> getRegistries(final EThingType type);

	/**
	 * Obtains the {@link Registry} that match the given type and id.
	 * 
	 * @param type      {@link EThingType} of the Registry.
	 * @param idBackend the Id of the Registry in the backend.
	 * @return the equivalent {@link RegistryDTO} of the found Registry, or
	 *         <code>null</code> if doesn't exist.
	 */
	public RegistryDTO getRegistriesByIdBackendAndType(final EThingType type, final Long idBackend);

	/**
	 * Creates the passed Registry. Only one {@link Registry} of type
	 * Gateway can be added.
	 * 
	 * @param registryDTO to be inserted.
	 * @return the {@link RegistryDTO} object created.
	 */
	public RegistryDTO createRegistry(final RegistryDTO registryDTO);

	/**
	 * Updated the passed Registry. 
	 * 
	 * @param registryDTO to be updated.
	 * @return the {@link RegistryDTO} object updated.
	 */
	public RegistryDTO updateRegistry(final RegistryDTO registryDTO);


	/**
	 * Deletes a {@link Registry} by the given type and id.
	 * 
	 * @param id   of the Registry.
	 * @param type {@link EThingType } that represents the type of the Registry.
	 */
	public void deleteRegistry(final Long id, final EThingType type);
	
	/**
	 * Used to initialize database task scheduler on Startup.
	 */
	public void initDbCleanerTasks();

	/**
	 * Returns the already configured Gateway if exists.
	 * 
	 * @return the gateway if exists, <code>null</code> otherwise.
	 */
	public RegistryDTO getGateway();
	
	/**
	 * Initializes the broker connection if the gateway already exists in the DB.
	 */
	public void initBrokerConnection();

}
