package com.uis.iot.common.gateway.model;

import java.util.Objects;

import org.springframework.core.convert.Property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent the {@link Property} as a Data Transfer Object
 * 
 * @author Camilo Gutiérrez
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDTO {

	private String name;

	private String value;

	private EPropertyType type;

	public PropertyDTO() {
		super();
	}

	public PropertyDTO(String name, String value, EPropertyType type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public EPropertyType getType() {
		return type;
	}

	public void setType(EPropertyType type) {
		this.type = type;
	}

	/**
	 * Checks if the Property can be edited. This implies it's not <code>null</code>
	 * or a Reported Property.
	 * 
	 * @return <code>true</code> if it's modifiable, <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isModifiable() {
		return type != EPropertyType.REPORTED;
	}

	/**
	 * Checks if the Property is valid.
	 * 
	 * @return <code>true</code> if it's valid, <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isValid() {
		return name != null && !name.trim().isEmpty() && value != null && !value.trim().isEmpty() && type != null;
	}

	/**
	 * Checks if the Property is a CONFIG property of the given
	 * {@link EConfigProperty}.
	 * 
	 * @param configProp to be compared.
	 * @return <code>true</code> if the property type matches, <code>false</code> if
	 *         it's not a CONFIG property or if the type is different.
	 */
	@JsonIgnore
	public boolean is(EConfigProperty configProp) {
		return type == EPropertyType.CONFIG && name != null && !name.trim().isEmpty()
				&& name.trim().equalsIgnoreCase(configProp.toString());
	}

	@Override
	public String toString() {
		return "[name=" + name + ", value=" + value + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof PropertyDTO) {
			PropertyDTO other = (PropertyDTO) obj;
			return Objects.equals(name, other.getName()) && Objects.equals(value, other.getValue())
					&& type == other.getType();

		}
		return false;
	}

}
