package co.uis.iot.edge.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import co.uis.iot.edge.common.model.EConfigProperty;
import co.uis.iot.edge.common.model.EPropertyType;
import co.uis.iot.edge.common.model.EReportedProperty;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.PropertyDTO;
import co.uis.iot.edge.core.persistence.Property;

/**
 * Utility methods for Process and ProcessDTO.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class PropertyUtil {
	
	private PropertyUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sanitizes the properties by checking their properties and uniqueness.
	 * 
	 * @param propertiesDTO list of properties to be checked.
	 * @return a correct list to be added in the database.
	 */
	public static List<PropertyDTO> sanitizeProperties(List<PropertyDTO> propertiesDTO) {
		if (CollectionUtils.isEmpty(propertiesDTO)) {
			return new ArrayList<>();
		}
		Map<String, PropertyDTO> propertiesMap = new HashMap<>();
		for (PropertyDTO propertyDTO : propertiesDTO) {
			if (propertyDTO.isValid() && propertyDTO.isModifiable()
					&& !propertiesMap.containsKey(propertyDTO.getName())) {
				propertiesMap.put(propertyDTO.getName().trim().toLowerCase(), propertyDTO);
			}
		}

		return new ArrayList<>(propertiesMap.values());
	}
	
	/**
	 * Filters a passed Collection of {@link PropertyDTO} for a given Property name.
	 * 
	 * @param properties to be filtered.
	 * @param type       of the desired property.
	 * @return the found Property as an {@link Optional}.
	 */
	public static Optional<PropertyDTO> getConfigPropertyByName(Collection<PropertyDTO> properties,
			EConfigProperty type) {
		return properties.stream().filter(property -> property.is(type)).findFirst();
	}

	/**
	 * Retrieves the topic (property value) for a given Process.
	 * 
	 * @param process to filter it's properties.
	 * @return the topic if exists. <code>null</code> otherwise.
	 */
	public static String getTopicIfExists(ProcessDTO process) {
		if (process == null) {
			return null;
		}
		return PropertyUtil.getConfigPropertyByName(process.getProperties(), EConfigProperty.TOPIC)
				.map(PropertyDTO::getValue).orElse(null);
	}

	/**
	 * Returns the send type (batch amount, batch frequency, or none) property.
	 * 
	 * @param process to be verified.
	 * @return an {@link Optional} wrapping the property if exists. An empty
	 *         {@link Optional} otherwise.
	 */
	public static Optional<PropertyDTO> getSendTypeProperty(ProcessDTO process) {
		return process.getProperties().stream().filter(
				property -> property.is(EConfigProperty.BATCH_AMOUNT) || property.is(EConfigProperty.BATCH_FREQUENCY))
				.findFirst();
	}

	/**
	 * Returns the PROCESS_JAR property.
	 * 
	 * @param process to be verified.
	 * @return an {@link Optional} wrapping the Property if exists. An empty
	 *         {@link Optional} otherwise.
	 */
	public static Optional<PropertyDTO> getProcessJarProperty(ProcessDTO process) {
		return process.getProperties().stream().filter(property -> property.is(EConfigProperty.PROCESS_JAR))
				.findFirst();
	}

	/**
	 * Checks if the given property is a BROKER_URL property.
	 * 
	 * @param property to be evaluated.
	 * @return <code>true</code> if the property is BROKER_URL, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isBrokerUrlProperty(Property property) {
		return property.getName().equalsIgnoreCase(EConfigProperty.BROKER_URL.toString())
				&& property.getType() == EPropertyType.CONFIG;
	}

	/**
	 * Obtains all Gateway's Reported Properties.
	 * 
	 * @return the reported properties.
	 */
	public static List<Property> getReportedProperties() {
		final List<Property> properties = new ArrayList<>();
		for (EReportedProperty reportedProperty : EReportedProperty.values()) {
			properties.add(new Property(reportedProperty.toString(), reportedProperty.getPropertyValue(),
					EPropertyType.REPORTED));
		}

		return properties;
	}
}
