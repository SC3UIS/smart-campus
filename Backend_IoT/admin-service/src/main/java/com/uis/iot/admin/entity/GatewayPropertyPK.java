package com.uis.iot.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.uis.iot.common.gateway.model.EPropertyType;

/**
 * The primary key class for the gateway_property database table.
 * 
 */
@Embeddable
public class GatewayPropertyPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String name;

	@Column(name = "id_gateway", nullable = false)
	private Long idGateway;

	@Enumerated(EnumType.STRING)
	@Column(name = "property_type", nullable = false)
	private EPropertyType propertyType;

	public GatewayPropertyPK() {
	}

	public GatewayPropertyPK(Long idGateway, EPropertyType propertyType, String name) {
		this.idGateway = idGateway;
		this.propertyType = propertyType;
		this.name = name;
	}

	public Long getIdGateway() {
		return this.idGateway;
	}

	public void setIdGateway(Long idGateway) {
		this.idGateway = idGateway;
	}

	public EPropertyType getNamePropertyType() {
		return this.propertyType;
	}

	public void setNamePropertyType(EPropertyType namePropertyType) {
		this.propertyType = namePropertyType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GatewayPropertyPK)) {
			return false;
		}
		GatewayPropertyPK castOther = (GatewayPropertyPK) other;
		return this.idGateway.equals(castOther.idGateway) && this.propertyType.equals(castOther.propertyType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idGateway.hashCode();
		hash = hash * prime + this.propertyType.hashCode();

		return hash;
	}
}