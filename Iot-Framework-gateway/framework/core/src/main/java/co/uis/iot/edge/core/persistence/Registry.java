package co.uis.iot.edge.core.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.PropertyDTO;
import co.uis.iot.edge.common.model.RegistryDTO;

/**
 * Represents a Thing Registry Document.
 * 
 * @author Camilo Gutiérrez
 *
 */
@Document(collection = "registry")
public class Registry extends SCDocument {

	/**
	 * The name of the Thing.
	 */
	private String name;
	
	/**
	 * The description of the Thing.
	 */
	private String description;

	/**
	 * The type of the Thing as an {@link EThingType}.
	 */
	@Indexed(unique = false)
	private EThingType type;

	/**
	 * A List of {@link Property}.
	 */
	@Indexed(name = "properties.name", unique = false)
	private List<Property> properties;

	public Registry() {
	}

	/**
	 * Creates an instance of a Registry.
	 * 
	 * @param id         of the Document.
	 * @param idBackend  that represents the Thing in the Backend.
	 * @param name       name of the Thing.
	 * @param description description of the Thing.
	 * @param type       of the Thing as an {@link EThingType}.
	 * @param properties of the Thing.
	 */
	public Registry(String id, Long idBackend, String name, String description, EThingType type, List<Property> properties) {
		super();
		this.id = id;
		this.idBackend = idBackend;
		this.name = name;
		this.description = description;
		this.type = type;
		this.properties = properties;
	}

	/**
	 * @return the name of the Thing.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of the Thing.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description of the Thing.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type of the Thing as a {@link EThingType}.
	 */
	public EThingType getType() {
		return type;
	}

	/**
	 * @param type of the Thing as a {@link EThingType}.
	 */
	public void setType(EThingType type) {
		this.type = type;
	}

	/**
	 * @return the list of Properties.
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * @param properties of the Thing.
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * Maps a given {@link RegistryDTO} to a Registry.
	 * 
	 * @param dto to be mapped.
	 * @return the equivalent {@link Registry}.
	 */
	public static Registry ofDTO(RegistryDTO dto) {
		List<Property> properties = new ArrayList<>();
		if (dto.getProperties() != null) {
			properties = dto.getProperties().stream()
					.map(property -> new Property(property.getName(), property.getValue(), property.getType()))
					.collect(Collectors.toList());
		}
		return new Registry(null, dto.getId(), dto.getName(), dto.getDescription(), dto.getType(), properties);
	}

	/**
	 * Maps the current Registry to a {@link GatewayDTO}
	 * 
	 * @return the mapped {@link GatewayDTO}
	 */
	public RegistryDTO toDTO() {
		List<PropertyDTO> deviceProperties = getProperties().stream().map(Property::toDTO)
				.collect(Collectors.toList());
		return new RegistryDTO(idBackend, name, description, deviceProperties, type);
	}

	@Override
	public String toString() {
		return "Registry [name=" + name + ", description=" + description + ", type=" + type + ", properties="
				+ properties + "]";
	}
}
