package com.uis.iot.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.iot.admin.service.IStatisticService;
import com.uis.iot.common.model.StatisticsDTO;

/**
 * Entry point for all Statistics (admin) related REST services.
 * 
 * @author Federico
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/statistics")
public class StatisticController {

	@Autowired
	private IStatisticService service;

	/**
	 * Gets all the statistics for a given user.
	 * 
	 * @param userId id of the user.
	 * @return a {@link ResponseEntity} wrapping a {@link List} of
	 *         {@link StatisticsDTO} statistics.
	 */
	@GetMapping(value = "/{userId}", produces = "application/json")
	public ResponseEntity<StatisticsDTO> getStatistics(@PathVariable(name = "userId") long userId) {
		return new ResponseEntity<>(service.getStatistics(userId), HttpStatus.OK);
	}

}
