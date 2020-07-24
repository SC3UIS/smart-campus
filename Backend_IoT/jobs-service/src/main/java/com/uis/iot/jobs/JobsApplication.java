package com.uis.iot.jobs;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.model.GatewayDTO;
import com.uis.iot.common.model.GatewayPropertyDTO;
import com.uis.iot.common.utils.CommonUtils;
import com.uis.iot.common.utils.ERoute;

@EnableAsync
@SpringBootApplication
public class JobsApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobsApplication.class);

	@Autowired
	private ApplicationContext context;
	
	@Value("${broker.url}")
	private String url;

	public static void main(String[] args) {
		SpringApplication.run(JobsApplication.class, args);
	}

	@Bean("threadPoolScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("JobThread-");
		scheduler.setPoolSize(50);
		return scheduler;
	}

	@PostConstruct
	public void postConstruct() {
		// Schedule the Keep Alive process (for gateways and processes) every 5 minutes.
		// Obtain the Bean from Spring context so @Autowired works inside the Runnable.
		threadPoolTaskScheduler().scheduleAtFixedRate(context.getBean(KeepAliveRunnable.class), new Date(System.currentTimeMillis() + 1000 * 60 * 5), 1000 * 60 * 5);
		verifyBrokerUrl();
	}
	
	public void verifyBrokerUrl() {
		for (int i = 0; i < 3; i++) {
			try {
				final ResponseEntity<GatewayDTO[]> response = CommonUtils
						.getHTTPResponse(ERoute.ADMIN + "gateways?processes=true", HttpMethod.GET, GatewayDTO[].class);
		
				final List<GatewayDTO> gateways = Arrays.asList(((GatewayDTO[]) response.getBody()));
				Assert.isTrue(!CollectionUtils.isEmpty(gateways), "No gateways registered, skipping Keep Alive process.");
				GatewayPropertyDTO property = gateways.get(0).getProperties().stream().filter(p -> p.getName().equals("broker_url")).findFirst().orElse(null);
				if (gateways != null && property.getValue() != url) {
					for (GatewayDTO gatewayDTO : gateways) {
						gatewayDTO.getProperties().stream().filter(p -> p.getName().equals("broker_url")).findFirst()
								.orElseThrow(() -> new InvalidKeyException("El gateway debe tener la propiedad broker_url"))
								.setValue(url);
						CommonUtils.getHTTPResponse(ERoute.ADMIN + "gateways/gateway/" + gatewayDTO.getId(), HttpMethod.PUT, GatewayDTO.class, gatewayDTO);
					}
				}
			} catch (final Exception e) {
				LOGGER.error("BrokerUrl veritification failed:", e);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
