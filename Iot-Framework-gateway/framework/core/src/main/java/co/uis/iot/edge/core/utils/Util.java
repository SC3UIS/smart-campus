package co.uis.iot.edge.core.utils;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class Util {

	private Util() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Transforms an {@link Object} into a JSON.
	 * 
	 * @param obj to be mapped.
	 * @return the JSON.
	 */
	public static String mapToJson(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	/**
	 * Transforms the given UTC date to the current timezone.
	 * 
	 * @param utcDate to be transformed. Not null.
	 * @return the Date in the current timezone.
	 */
	@SuppressWarnings("deprecation")
	public static Date utcToLocalDate(Date utcDate) {
		return new Date(utcDate.getTime() - (utcDate.getTimezoneOffset() * 60000));
	}
}
