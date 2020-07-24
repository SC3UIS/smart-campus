package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.springframework.util.CollectionUtils;

import com.uis.iot.common.gateway.model.PropertyDTO;
import com.uis.iot.common.model.ProcessDTO;

/**
 * The persistent class for the process database table.
 * 
 */
@Entity
@NamedQuery(name = "Process.findAll", query = "SELECT p FROM Process p")
public class Process implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_process")
	private Long id;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String name;

	@Column(name = "is_alive")
	private boolean alive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_gateway", updatable = false, insertable = false)
	private Gateway gateway;

	/**
	 * Required for insert and update operations.
	 */
	@Column(name = "id_gateway")
	private Long gatewayId;

	@OneToMany(mappedBy = "process")
	private List<Notification> notifications = new ArrayList<>(0);

	@OneToMany(mappedBy = "process", cascade = CascadeType.ALL, targetEntity = ProcessProperty.class, orphanRemoval = true)
	private List<ProcessProperty> processProperties = new ArrayList<>(0);

	/**
	 * Default constructor.
	 */
	public Process() {
		super();
	}

	/**
	 * Creates an instance of process.
	 * 
	 * @param id          of the Process.
	 * @param description of the Process.
	 * @param name        of the Process.
	 * @param alive       indicates if the Process is alive or not.
	 */
	public Process(Long id, String description, String name, boolean alive) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.alive = alive;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public List<ProcessProperty> getProcessProperties() {
		return processProperties;
	}

	public void setProcessProperties(List<ProcessProperty> processProperties) {
		this.processProperties.clear();
		this.processProperties.addAll(processProperties);
	}

	/**
	 * Creates a {@link Process} from a {@link ProcessDTO}.
	 * 
	 * @param processDTO DTO of the Process to be mapped.
	 * @return new Process entity.
	 */
	public static Process fromDTO(ProcessDTO processDTO) {
		final Process process = new Process(processDTO.getId(), processDTO.getDescription(), processDTO.getName(),
				true);
		process.setGateway(new Gateway(processDTO.getGatewayId(), null, null, null, true));
		process.setGatewayId(processDTO.getGatewayId());
		process.setProcessProperties(
				processDTO.getProperties().stream().map(ProcessProperty::fromDTO).collect(Collectors.toList()));
		return process;
	}

	/**
	 * Maps the entity to a {@link ProcessDTO}.
	 * 
	 * @return DTO of the entity.
	 */
	public ProcessDTO toDTO() {
		ProcessDTO processDTO = new ProcessDTO(this.id, this.name, this.getGatewayId(), this.description,
				this.isAlive());
		processDTO.setProperties(processProperties.stream().map(ProcessProperty::toDTO).collect(Collectors.toList()));
		return processDTO;
	}

	/**
	 * Maps the entity to a DTO for the Gateway.
	 * 
	 * @return the DTO, for the Gateway.
	 */
	public com.uis.iot.common.gateway.model.ProcessDTO toDTOforGateway() {
		return new com.uis.iot.common.gateway.model.ProcessDTO(id, name, description,
				processProperties.stream().map(ProcessProperty::toDTOforGateway).collect(Collectors.toList()));
	}

	/**
	 * Transforms a Process DTO (from Gateway) into a Process.
	 * 
	 * @param processDTO DTO of the Process in the Gateway.
	 * @return the equivalent process.
	 */
	public static Process fromDTOfromGateway(com.uis.iot.common.gateway.model.ProcessDTO processDTO, long gatewayId) {
		final List<ProcessProperty> props = new ArrayList<>();
		if (!CollectionUtils.isEmpty(processDTO.getProperties())) {
			for (PropertyDTO prop : processDTO.getProperties()) {
				props.add(ProcessProperty.fromDTOfromGateway(prop, processDTO.getId()));
			}
		}

		final Process process = new Process(processDTO.getId(), processDTO.getDescription(), processDTO.getName(),
				true);
		process.setGatewayId(gatewayId);
		process.setProcessProperties(processDTO.getProperties().stream()
				.map(prop -> ProcessProperty.fromDTOfromGateway(prop, processDTO.getId()))
				.collect(Collectors.toList()));
		return process;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, description, name, alive);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Process) {
			Process other = (Process) obj;
			return Objects.equals(id, other.getId());
		}
		return false;
	}

}