package com.uis.iot.common.gateway.model;

/**
 * Represents the Configuration Property Types.
 * 
 * @author Camilo Gutierrez.
 *
 */
public enum EConfigProperty {

	TOPIC("topic"), BATCH_FREQUENCY("batch_frequency"), BATCH_AMOUNT("batch_amount"),
	DB_CLEANUP_TIME("db_cleanup_time"), BROKER_URL("broker_url"), PROCESS_JAR("process_jar");

	private String name;

	private EConfigProperty(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
