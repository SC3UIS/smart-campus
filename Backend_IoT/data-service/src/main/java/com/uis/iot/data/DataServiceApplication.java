package com.uis.iot.data;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.uis.iot.data.service.ActiveMQService;

@SpringBootApplication
public class DataServiceApplication {
	
	@Autowired
	private ActiveMQService activeMQService;

	public static void main(String[] args) {
		SpringApplication.run(DataServiceApplication.class, args);
	}
	
	@PostConstruct void init() {
		activeMQService.subscribeAll();
	}
}
