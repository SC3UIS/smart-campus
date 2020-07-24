package com.uis.iot.admin.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uis.iot.admin.entity.Device;

public interface IDeviceRepository extends PagingAndSortingRepository<Device, Long> {

	/**
	 * Finds the devices that belong to the given gateway with a specified Sort.
	 * 
	 * @param gatewayId id of the gateway.
	 * @param sort      {@link Sort} to execute any sorting.
	 * @return Gateway's devices. Never <code>null</code>.
	 */
	public List<Device> findByGatewayId(long gatewayId, Sort sort);

	/**
	 * Finds the devices that belong to the given user.
	 * 
	 * @param userId id of the user.
	 * @return User's devices. Never <code>null</code>.
	 */
	public List<Device> findByGatewayUserId(long userId, Sort sort);

	/**
	 * Retrieves the amount of devices for a given user.
	 * 
	 * @param userId id of the user.
	 * @return the amount of devices.
	 */
	public long countByGatewayUserId(long userId);

}
