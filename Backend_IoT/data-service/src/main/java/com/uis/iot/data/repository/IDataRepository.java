package com.uis.iot.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.uis.iot.data.entity.Data;

@Repository
public interface IDataRepository extends MongoRepository<Data, String> {
	
	List<Data> findByProcessIdInAndTimestampBetween(List<Long> processes, Date initialDate, Date endDate);
	
	List<Data> findByTimestampBetween(Date initialDate, Date endDate);
	
	List<Data> findByProcessIdIn(List<Long> processes);
}
