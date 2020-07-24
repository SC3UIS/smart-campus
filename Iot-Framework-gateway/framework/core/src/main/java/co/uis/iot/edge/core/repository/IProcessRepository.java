package co.uis.iot.edge.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uis.iot.edge.core.persistence.Process;

/**
 * Repository used for operations over {@link Process} entity.
 * 
 * @author Camilo Gutiérrez.
 *
 */
public interface IProcessRepository extends MongoRepository<Process, Long> {

	/**
	 * Find a Process by its id in the Backend solution.
	 * 
	 * @param idBackend id of the Process in the Backend.
	 * @return an {@link Optional} that wraps the query result.
	 */
	public Optional<Process> findByIdBackend(Long idBackend);

	/**
	 * Find Processes by its deployed status.
	 * 
	 * @param deployed <code>true</code> to query the deployed jars,
	 *                 <code>false</code> otherwise.
	 * @return the found Processes.
	 */
	public List<Process> findByDeployed(boolean deployed);

	/**
	 * Deletes a Process with the given id.
	 * 
	 * @param idBackend the id sent from the backend.
	 * @return the amount of deleted processes.
	 */
	public Long deleteByIdBackend(Long idBackend);

}
