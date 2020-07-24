package com.uis.iot.admin.service;

import java.util.List;

import com.uis.iot.admin.entity.Device;
import com.uis.iot.common.model.DeviceDTO;
import com.uis.iot.common.model.ResponseDTO;

public interface IDeviceService {
	/**
	 * @param device
	 * @return
	 */
	public Device registerDevice(Device device);

	/**
	 * @param deviceDTO
	 * @param idDevice
	 * @return
	 */
	public DeviceDTO editDevice(DeviceDTO deviceDTO, long idDevice);

	/**
	 * @param idDevice
	 * @return
	 */
	public ResponseDTO deleteDevice(long idDevice);

	/**
	 * @param idDevice
	 * @return
	 */
	public DeviceDTO getDevice(long idDevice);

	/**
	 * Retrieves the devices that belong to a gateway identified by its id.
	 * 
	 * @param gatewayId - Gateway id to query the applications.
	 * @return the list of devices that belong to the given gateway, not
	 *         <code>null</code>.
	 */
	public List<Device> getDevicesByGateway(final long gatewayId);

	/**
	 * Retrieves the devices that belong to any of the gateways of an Application identified by its id.
	 * 
	 * @param idApplication
	 * @return
	 */
	public List<Device> getDevicesByApplication(long idApplication);

	/**
	 * Retrieves the devices that belong to any of the gateways of any of the Applications of a give user,
	 * identified by their id.
	 * @param idUser
	 * @return
	 */
	public List<Device> getDevicesByUser(long idUser);
}