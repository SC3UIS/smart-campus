package co.uis.iot.edge.core.jobs;

import co.uis.iot.edge.core.service.IStorageService;

/**
 * Runnable used to clean messages that were already sent from the DB.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class StorageCleanerRunnable implements Runnable {
	
	private IStorageService storageService; 
	
	/**
	 * Constructs the StorageCleanerRunnable.
	 * 
	 * @param storageService (singleton) interface to send clean messages.
	 */
	public StorageCleanerRunnable(IStorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public void run() {
		storageService.deleteMessagesBySentStatus(true);
	}
}
