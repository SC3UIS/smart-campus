package co.uis.iot.edge.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.uis.iot.edge.core.service.IProcessService;
import co.uis.iot.edge.core.service.IRegistryService;

/**
 * StartUp Thread that initializes the the Task Schedulers and Deployed
 * Processes.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class StartupThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupThread.class);

	private IProcessService processService;

	private IRegistryService registryService;

	/**
	 * Creates an instance of {@link StartupThread}.
	 * 
	 * @param processService  reference to {@link IProcessService}.
	 * @param registryService reference to {@link IRegistryService}.
	 */
	public StartupThread(IProcessService processService, IRegistryService registryService) {
		this.processService = processService;
		this.registryService = registryService;
		setName("StartupThread");
		LOGGER.info("Initializing Startup Thread");
	}

	@Override
	public void run() {
		processService.initBatchFrequencyTasks();
		registryService.initDbCleanerTasks();
		processService.initDeployedProcesses();
	}
}
