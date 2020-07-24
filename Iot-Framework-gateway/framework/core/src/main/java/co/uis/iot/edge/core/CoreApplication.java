package co.uis.iot.edge.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import co.uis.iot.edge.core.service.IProcessService;
import co.uis.iot.edge.core.service.IRegistryService;
import co.uis.iot.edge.core.utils.StartupThread;
import co.uis.iot.edge.core.vertx.IVertxHandler;
import io.vertx.core.Vertx;

@SpringBootApplication
@EnableCaching
public class CoreApplication {
	
	@Autowired
	private IProcessService processService;
		
	@Autowired
	private IRegistryService registryService;
	
	@Autowired
	private IVertxHandler vertxHandler;
	
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	@Bean
    public TaskScheduler threadPoolTaskScheduler() {
		return new ThreadPoolTaskScheduler();
    }
	
	@PostConstruct
	public void startup() {
		registryService.initBrokerConnection();
		Vertx.vertx().deployVerticle(vertxHandler);
		new StartupThread(processService, registryService).start();
	}
}
