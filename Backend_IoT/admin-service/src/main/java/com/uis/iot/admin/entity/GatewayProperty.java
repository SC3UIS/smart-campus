package com.uis.iot.admin.entity;

import java.io.Serializable;

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
import com.uis.iot.common.model.GatewayPropertyDTO;

/**
 * The persistent class for the gateway_property database table.
 * 
 */
@Entity
@Table(name = "gateway_property")
@NamedQuery(name = "GatewayProperty.findAll", query = "SELECT g FROM GatewayProperty g")
public class GatewayProperty implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private GatewayPropertyPK id;

	private String value;

	// bi-directional many-to-one association to Gateway
	@MapsId("idGateway")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_gateway", referencedColumnName = "id_gateway", insertable = false, updatable = false)
	private Gateway gateway;

	public GatewayProperty() {
		super();
	}

	public GatewayProperty(final EPropertyType propertyType, final Long gatewayId, final String name, final String value) {
		this.id = new GatewayPropertyPK(gatewayId, propertyType, name);
		this.value = value;
	}

	public GatewayPropertyPK getId() {
		return this.id;
	}

	public void setId(final GatewayPropertyPK id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Gateway getGateway() {
		return this.gateway;
	}

	public void setGateway(final Gateway gateway) {
		this.gateway = gateway;
	}

	public static GatewayProperty fromDTO(final GatewayPropertyDTO propertyDTO) {
		return new GatewayProperty(propertyDTO.getType(), propertyDTO.getGatewayId(), propertyDTO.getName(),
				propertyDTO.getValue());
	}

	public GatewayPropertyDTO toDTO() {
		return new GatewayPropertyDTO(id.getNamePropertyType(), id.getIdGateway(), id.getName(), value);
	}

	public PropertyDTO toDTOforGateway() {
		return new PropertyDTO(id.getName(), value, id.getNamePropertyType());
	}

	public static GatewayProperty fromDTOfromGateway(final PropertyDTO propertyDTO, final Long idGateway) {
		return new GatewayProperty(propertyDTO.getType(), idGateway, propertyDTO.getName(), propertyDTO.getValue());
	}
}