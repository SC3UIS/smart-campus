package co.uis.iot.edge.core.persistence;

import org.springframework.util.StringUtils;

import co.uis.iot.edge.common.model.EPropertyType;
import co.uis.iot.edge.common.model.PropertyDTO;

/**
 * Represents the Property as it's persisted (an Embedded Mongo Document).
 * 
 * @author Camilo Gutiérrez
 *
 */
public class Property {

	/**
	 * The name of the Property.
	 */
	private String name;

	/**
	 * The value of the Property as a {@link String}.
	 */
	private String value;

	/**
	 * The type of the Property.
	 */
	private EPropertyType type;

	/**
	 * Creates a new instance of a Property.
	 * 
	 * @param name  of the property
	 * @param value of the property as a {@link String}
	 * @param type  of the property as a {@link EPropertyType}
	 */
	public Property(String name, String value, EPropertyType type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}

	/**
	 * @return the name of the Property.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of the Property.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value of the Property.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value of the Property.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type of the Property.
	 */
	public EPropertyType getType() {
		return type;
	}

	/**
	 * @param type of the Property.
	 */
	public void setType(EPropertyType type) {
		this.type = type;
	}

	/**
	 * Creates a Property from a {@link PropertyDTO}.
	 * 
	 * @param propertyDTO to be mapped.
	 * @return the created Property.
	 */
	public static Property ofDTO(PropertyDTO propertyDTO) {
		return new Property(propertyDTO.getName(), propertyDTO.getValue(), propertyDTO.getType());
	}

	/**
	 * Maps the Property to a {@link PropertyDTO}
	 * 
	 * @return the Property as a {@link PropertyDTO}.
	 */
	public PropertyDTO toDTO() {
		return new PropertyDTO(getName(), getValue(), getType());
	}

	/**
	 * Checks if the Property can be edited. If it's not <code>null</code> or
	 * a Reported Property.
	 * 
	 * @return <code>true</code> if it's modifiable, false otherwise.
	 */
	public boolean isModifiable() {
		return type != null && type != EPropertyType.REPORTED;
	}
	
	/**
	 * Checks if the Property is valid.
	 * 
	 * @return <code>true</code> if it's valid, false otherwise.
	 */
	public boolean isValid() {
		return !StringUtils.isEmpty(name) && !StringUtils.isEmpty(value) && type != null;
	}

	@Override
	public String toString() {
		return "Property [name=" + name + ", value=" + value + ", type=" + type + "]";
	}
}
