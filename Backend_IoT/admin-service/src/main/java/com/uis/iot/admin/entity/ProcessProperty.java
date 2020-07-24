package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.uis.iot.common.gateway.model.EPropertyType;
import com.uis.iot.common.gateway.model.PropertyDTO;
import com.uis.iot.common.model.ProcessPropertyDTO;

/**
 * The persistent class for the process_property database table.
 * 
 */
@Entity
@Table(name = "process_property")
@NamedQuery(name = "ProcessProperty.findAll", query = "SELECT p FROM ProcessProperty p")
public class ProcessProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProcessPropertyPK id;

	private String value;

	@MapsId("processId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_process", referencedColumnName = "id_process", insertable = false, updatable = false)
	private Process process;

	/**
	 * Default constructor.
	 */
	public ProcessProperty() {
		super();
	}

	/**
	 * Creates an instance of ProcessProperty.
	 * 
	 * @param type      {@link EPropertyType} of the Property.
	 * @param idProcess id of the Process that owns the Property.
	 * @param name      of the Property.
	 * @param value     of the Property.
	 */
	public ProcessProperty(EPropertyType type, Long idProcess, String name, String value) {
		this.id = new ProcessPropertyPK(idProcess, type, name);
		this.value = value;
	}

	public ProcessPropertyPK getId() {
		return this.id;
	}

	public void setId(ProcessPropertyPK id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Process getProcess() {
		return this.process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * Creates a {@link ProcessProperty} from a {@link ProcessPropertyDTO}
	 * 
	 * @param propertyDTO DTO of the Property to be mapped.
	 * @return the equivalent entity.
	 */
	public static ProcessProperty fromDTO(ProcessPropertyDTO propertyDTO) {
		return new ProcessProperty(propertyDTO.getType(), propertyDTO.getProcessId(), propertyDTO.getName(),
				propertyDTO.getValue());
	}

	/**
	 * Maps the entity into a {@link ProcessPropertyDTO}.
	 * 
	 * @return the DTO for the entity.
	 */
	public ProcessPropertyDTO toDTO() {
		return new ProcessPropertyDTO(id.getType(), id.getProcessId(), id.getName(), value);
	}

	/**
	 * Maps the Property into a {@link PropertyDTO} used by the Gateway.
	 * 
	 * @return the {@link PropertyDTO}.
	 */
	public PropertyDTO toDTOforGateway() {
		return new PropertyDTO(id.getName(), value, id.getType());
	}

	/**
	 * Creates a {@link ProcessProperty} from a {@link ProcessProperty} from the
	 * Gateway.
	 * 
	 * @param propertyDTO DTO to be mapped.
	 * @param idProcess   id of the Process owner of the Property.
	 * @return the {@link ProcessProperty}
	 */
	public static ProcessProperty fromDTOfromGateway(PropertyDTO propertyDTO, Long idProcess) {
		return new ProcessProperty(propertyDTO.getType(), idProcess, propertyDTO.getName(), propertyDTO.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, process, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof ProcessProperty) {
			ProcessProperty other = (ProcessProperty) obj;
			return Objects.equals(id, other.getId()) && Objects.equals(process, other.getProcess())
					&& Objects.equals(value, other.getValue());
		}
		return false;
	}

}