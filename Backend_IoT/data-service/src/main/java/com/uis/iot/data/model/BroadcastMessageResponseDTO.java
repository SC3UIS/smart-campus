package com.uis.iot.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author RoJo
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BroadcastMessageResponseDTO {
	/**
	 * The gateway id.
	 */
    private String gatewayIp;
    /**
     * The error message thrown by the gateway.
     */
    private String errorMessage;
    
    public BroadcastMessageResponseDTO(String gatewayIp, String errorMessage) {
    	this.gatewayIp = gatewayIp;
    	this.errorMessage = errorMessage;
    }

    public String getGatewayIp() {
		return gatewayIp;
	}

	public void setGatewayIp(String gatewayIp) {
		this.gatewayIp = gatewayIp;
	}

	/**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
  
}
