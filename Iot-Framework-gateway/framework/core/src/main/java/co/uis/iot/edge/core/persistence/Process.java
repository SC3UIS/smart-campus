package co.uis.iot.edge.core.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.PropertyDTO;

/**
 * Represents the Process (Use case) as it's persisted.
 *
 * @author Camilo Gutiérrez
 */
@Document(collection = "process")
public class Process extends SCDocument {

	/**
	 * The name of the process.
	 */
	private String name;
	
	/**
	 * The process' description.
	 */
	private String description;
	
	/**
	 * Indicates if the process was already deployed at least once.
	 */
	private boolean deployed = false;

	/**
	 * A List of {@link Property}.
	 */
	@Indexed(name = "properties.name", unique = false)
	private List<Property> properties;
	
	public Process() {
	}

	/**
	 * Creates an instance of a Process.
	 * 
	 * @param id          of the document.
	 * @param idBackend   id of the Process in the backend.
	 * @param name        of the Process.
	 * @param description of the Process.
	 * @param deployed    indicates whether the Process was deployed or not.
	 * @param properties  of the Process.
	 */
	public Process(String id, Long idBackend, String name, String description, boolean deployed,
			List<Property> properties) {
		super();
		this.id = id;
		this.idBackend = idBackend;
		this.name = name;
		this.description = description;
		this.deployed = deployed;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * Maps the given {@link ProcessDTO} to a Process.
	 * 
	 * @param dto to be mapped.
	 * @return the equivalent {@link Process}.
	 */
	public static Process ofDTO(ProcessDTO dto) {
		List<Property> properties = new ArrayList<>();
		if (dto.getProperties() != null) {
			properties = dto.getProperties().stream()
					.map(property -> new Property(property.getName(), property.getValue(), property.getType()))
					.collect(Collectors.toList());
		}
		return new Process(null, dto.getId(), dto.getName(), dto.getDescription(), dto.isDeployed(), properties);
	}

	/**
	 * Maps the current Process to a {@link ProcessDTO}
	 * 
	 * @return the mapped {@link ProcessDTO}
	 */
	public ProcessDTO toDTO() {
		List<PropertyDTO> processProperties = getProperties().stream().map(Property::toDTO)
				.collect(Collectors.toList());
		return new ProcessDTO(idBackend, name, description, deployed, processProperties);
	}

	@Override
	public String toString() {
		return "Process [name=" + name + ", description=" + description + ", properties=" + properties + "]";
	}
}
