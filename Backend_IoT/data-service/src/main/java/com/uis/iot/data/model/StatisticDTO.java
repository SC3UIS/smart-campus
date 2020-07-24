package com.uis.iot.data.model;

import org.bson.Document;

public class StatisticDTO {
	private StatisticPkDTO id;
	private Integer count;
	
	public StatisticDTO() {
		id = new StatisticPkDTO();
	}
	
	public StatisticPkDTO getId() {
		return id;
	}
	
	public void setId(StatisticPkDTO id) {
		this.id = id;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public static StatisticDTO fromDocument(Document document) {
		StatisticDTO statisticDTO = new StatisticDTO();
		Document _id = ((Document) document.get("_id"));
		if (_id != null) {
			statisticDTO.getId().setGatewayId(_id.getLong("gatewayId"));
			statisticDTO.getId().setProcessId(_id.getLong("processId"));
			statisticDTO.getId().setHour(_id.getInteger("hour"));
			statisticDTO.getId().setDayOfWeek(_id.getInteger("dayOfWeek"));
			statisticDTO.getId().setDayOfMonth(_id.getInteger("dayOfMonth"));
			statisticDTO.getId().setMonth(_id.getInteger("month"));
		}
		statisticDTO.setCount(document.getInteger("count"));
		return statisticDTO;
	}
}
