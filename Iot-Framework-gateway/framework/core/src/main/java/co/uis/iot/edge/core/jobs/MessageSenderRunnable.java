package co.uis.iot.edge.core.jobs;

import co.uis.iot.edge.core.service.ICommunicationService;

/**
 * Runnable used to send Messages using batch frequency.
 * 
 * @author Camilo Gutiérrez
 *
 */
public class MessageSenderRunnable implements Runnable {
	
	private ICommunicationService communicationService; 
	
	private Long processId;
	
	/**
	 * Constructs the MessageSenderRunnable.
	 * 
	 * @param processId processId to check every time.
	 * @param communicationService (singleton) interface to send mqtt messages.
	 */
	public MessageSenderRunnable(Long processId, ICommunicationService communicationService) {
		this.processId = processId;
		this.communicationService = communicationService;
	}

	@Override
	public void run() {
		communicationService.sendActiveMQBatch(processId);
	}

}
