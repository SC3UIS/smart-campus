package com.uis.iot.admin.service;

import com.uis.iot.common.model.StatisticsDTO;

/**
 * Definition of Statistic related business logic.
 * 
 * @author Federico
 *
 */
public interface IStatisticService {

	/**
	 * Retrieves all the statistics for a given user.
	 * 
	 * @param userId the id of the user.
	 * @return the statistics as a {@link StatisticsDTO}.
	 */
	public StatisticsDTO getStatistics(final long userId);

}
