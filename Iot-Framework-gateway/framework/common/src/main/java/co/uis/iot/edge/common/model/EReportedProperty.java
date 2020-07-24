package co.uis.iot.edge.common.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Contains the Reported Properties.
 * 
 * @author Camilo Gutiérrez
 *
 */
public enum EReportedProperty {
	OS("SO", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("os.name");
		}
	},
	OS_VERSION("Versión SO", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("os.version");
		}
	},
	OS_ARCH("Arquitectura SO", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("os.arch");
		}
	},
	CORES("Núcleos", false) {
		@Override
		public String getPropertyValue() {
			return String.valueOf(Runtime.getRuntime().availableProcessors());
		}
	},
	JVM_MEMORY("Memoria JVM", true) {
		@Override
		public String getPropertyValue() {
			return String.valueOf(Runtime.getRuntime().totalMemory() / (float) MEGABYTES);
		}
	},
	SPACE("Espacio disponible", true) {
		@Override
		public String getPropertyValue() {
			 List<File> roots = Arrays.asList(File.listRoots());
			 return String.valueOf(roots.stream().map(root -> root.getFreeSpace() / MEGABYTES).reduce(0L, Long::sum));
		}
	},
	COUNTRY("País", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("user.country");
		}
	},
	JAVA_VERSION("Versión Java", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("java.version");
		}
	},
	TIMEZONE("Zona Horaria", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("user.timezone");
		}
	},
	LANGUAGE("Idioma", false) {
		@Override
		public String getPropertyValue() {
			return System.getProperty("user.language");
		}
	};

	private static final long MEGABYTES = 1024L * 1024;
	
	private String name;

	private boolean isVariable;
	
	private EReportedProperty(String name, boolean isVariable) {
		this.name = name;
		this.isVariable = isVariable;
	}

	/**
	 * Retrieves the property value.
	 * 
	 * @return a String that contains the property.
	 */
	public abstract String getPropertyValue();

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Determines if the Reported property is variable or constant.
	 * 
	 * @return true if the property changes, false otherwise.
	 */
	public boolean isVariable() {
		return isVariable;
	}

}
