package com.uis.iot.admin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import com.uis.iot.common.model.ApplicationDTO;
import com.uis.iot.common.model.GatewayDTO;

/**
 * The persistent class for the application database table.
 * 
 */
@Entity
@NamedQuery(name = "Application.findAll", query = "SELECT a FROM Application a")
public class Application implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_application")
	private Long id;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false, unique = true)
	private String name;

	// bi-directional many-to-one association to AppUser
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user")
	private AppUser user;

	// bi-directional many-to-many association to Gateway
	@ManyToMany
	@JoinTable(name = "application_per_gateway", joinColumns = {
			@JoinColumn(name = "id_application") }, inverseJoinColumns = { @JoinColumn(name = "id_gateway") })
	private List<Gateway> gateways;

	public Application() {
		super();
	}

	public Application(Long id, String name, AppUser user, String description) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.user = user;
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

	public AppUser getAppUser() {
		return this.user;
	}

	public void setAppUser(AppUser appUser) {
		this.user = appUser;
	}

	public List<Gateway> getGateways() {
		if (this.gateways == null) {
			this.gateways = new ArrayList<>(1);
		}
		return this.gateways;
	}

	public void setGateways(List<Gateway> gateways) {
		this.gateways = gateways;
	}

	public ApplicationDTO toDTO() {
		return new ApplicationDTO(id, name, user.getId(), description,
				gateways != null ? gateways.stream()
						.map(gateway -> new GatewayDTO(gateway.getId(), gateway.getIpGateway(),
								gateway.getDescription(), gateway.getName(), gateway.isAlive()))
						.collect(Collectors.toList()) : null);
	}

	public static Application fromDTO(ApplicationDTO dto) {
		return new Application(dto.getId(), dto.getName(), new AppUser(dto.getUserId()), dto.getDescription());
	}

}