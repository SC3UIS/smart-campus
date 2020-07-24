package com.uis.iot.data.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uis.iot.data.model.StatisticDTO;

@Repository
public class StatisticRepository {
	@Value("${spring.data.mongodb.host}")
	private String host;
	
	@Value("${spring.data.mongodb.port}")
	private String port;
	
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	@Value("${spring.data.mongodb.user}")
	private String user;
	
	@Value("${spring.data.mongodb.password}")
	private String password;
	
	private MongoClient getConnection() {
		boolean auth = user != null && !user.equals("") && password != null && !password.equals("");
		String uri = "mongodb://";
		if (auth) {
			uri += user + ":" + password +  "@";
		}
		uri += host +":" + port;
		if (auth) {
			uri += "/" + database;
		}
		MongoClient mongoClient = MongoClients.create(uri);
		return mongoClient;
	}
	
	public List<StatisticDTO> getStatistics(List<Integer> gatewaysId, List<Integer> processesId) {
		List<StatisticDTO> statistics = new ArrayList<>();
		MongoClient client = getConnection();
		MongoDatabase db = client.getDatabase(database);
		MongoCollection<Document> dataCollection = db.getCollection("data");
		Document filterStage = new Document("$match", new Document("gatewayId", 
				new Document("$in", gatewaysId))
			.append("processId", 
				new Document("$in", processesId)));
		
		// By Gateway
		// Gateway hour
		AggregateIterable<Document> documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("gatewayId", "$gatewayId")
											.append("hour", 
													new Document("$hour", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.hour", 1)
										.append("gatewayId", 1))
				));
		Iterator<Document> iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Gateway DayOfWeek
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("gatewayId", "$gatewayId")
											.append("dayOfWeek", 
													new Document("$dayOfWeek", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.dayOfWeek", 1)
						.append("gatewayId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Gateway DayOfMonth
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("gatewayId", "$gatewayId")
											.append("dayOfMonth", 
													new Document("$dayOfMonth", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.dayOfMonth", 1)
						.append("gatewayId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Gateway month
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("gatewayId", "$gatewayId")
											.append("month", 
													new Document("$month", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.month", 1)
						.append("gatewayId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}		
		
		// By Process
		// Process hour
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("processId", "$processId")
											.append("hour", 
													new Document("$hour", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.hour", 1)
						.append("processId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Process DayOfWeek
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("processId", "$processId")
											.append("dayOfWeek", 
													new Document("$dayOfWeek", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.dayOfWeek", 1)
						.append("processId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Process DayOfMonth
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("processId", "$processId")
											.append("dayOfMonth", 
													new Document("$dayOfMonth", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.dayOfMonth", 1)
						.append("processId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}
		
		// Process month
		documents = dataCollection.aggregate(Arrays.asList(
				filterStage,
				new Document("$group", new Document("_id", 
											new Document("processId", "$processId")
											.append("month", 
													new Document("$month", "$timestamp")))
						.append("count", new Document("$sum", 1))),
				new Document("$sort", new Document("_id.month", 1)
						.append("processId", 1))
				));
		iterator = documents.iterator();
		while(iterator.hasNext()) {
			Document document = iterator.next();
			statistics.add(StatisticDTO.fromDocument(document));
		}	
		client.close();
		return statistics;
	}
}
