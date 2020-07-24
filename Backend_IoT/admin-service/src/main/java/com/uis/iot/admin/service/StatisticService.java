package com.uis.iot.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uis.iot.admin.repository.IApplicationRepository;
import com.uis.iot.admin.repository.IDeviceRepository;
import com.uis.iot.admin.repository.IGatewayRepository;
import com.uis.iot.admin.repository.IProcessRepository;
import com.uis.iot.admin.repository.StatisticRepository;
import com.uis.iot.common.model.StatisticsDTO;

/**
 * Implementation of Statistics business layer.
 * 
 * @author Federico
 *
 */
@Service
public class StatisticService implements IStatisticService {

	@Autowired
	private IApplicationRepository applicationRepository;

	@Autowired
	private IGatewayRepository gatewayRepository;

	@Autowired
	private IProcessRepository processRepository;

	@Autowired
	private IDeviceRepository deviceRepository;

	@Autowired
	private StatisticRepository statisticRepository;

	@Override
	@Transactional(readOnly = true)
	public StatisticsDTO getStatistics(long userId) {
		final StatisticsDTO stats = new StatisticsDTO();
		stats.setApplications(applicationRepository.countByUserId(userId));
		stats.setGatewaysAlive(gatewayRepository.countByUserIdAndAliveTrue(userId));
		stats.setGatewaysDeath(gatewayRepository.countByUserIdAndAliveFalse(userId));
		stats.setProcessesAlive(processRepository.countByGatewayUserIdAndAliveTrue(userId));
		stats.setProcessesDeath(processRepository.countByGatewayUserIdAndAliveFalse(userId));
		stats.setDevices(deviceRepository.countByGatewayUserId(userId));
		stats.setChanges(statisticRepository.getChanges(userId));
		return stats;
	}

}
