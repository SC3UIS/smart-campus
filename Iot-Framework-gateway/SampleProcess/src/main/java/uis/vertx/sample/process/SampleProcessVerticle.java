package uis.vertx.sample.process;

import java.util.List;
import java.util.function.Consumer;

import co.uis.iot.edge.common.model.EThingType;
import co.uis.iot.edge.common.model.EventBusResponseDTO;
import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.ProcessAliveDTO;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.ProcessVerticle;
import co.uis.iot.edge.common.model.RegistryDTO;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SampleProcessVerticle extends ProcessVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleProcessVerticle.class);

	public SampleProcessVerticle() {
		super(1L, "proceso1");
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		this.initConsumers();
		getRegistersLogic();
		getProcessLogic();
		sendMessageLogic();
		saveMessageLogic();
		getMessagesByProcessIdLogic();

		startFuture.complete();
	}

	@Override
	public void messageHandler(MessageDTO messageDTO) {
		LOGGER.info(messageDTO.getPayload());
	}

	@Override
	public ProcessAliveDTO processKeepAlive() {
		LOGGER.info("Verificando el estado del proceso...");
		return new ProcessAliveDTO(true, "The process 1 is currently working.");
	}

	/**
	 * Calls the saveMessage from the superclass in order to save messages through
	 * the event bus.
	 */
	private void saveMessageLogic() {
		Consumer<EventBusResponseDTO> saveMessageResponseHandler = responseDTO -> {
			if (responseDTO.getSucceeded()) {
				LOGGER.info("[saveMessage] Successfuly Response: " + responseDTO.getBody());
			} else {
				LOGGER.error("[saveMessage] Error Response: " + responseDTO.getBody());
			}
			// Do something with the response.
		};

		this.saveMessage(this.processId, "Mensaje de prueba guardado desde proceso 1 a través del Event Bus.",
				saveMessageResponseHandler);
	}

	/**
	 * Calls the sendMessage from the superclass in order to send messages through
	 * the event bus.
	 */
	private void sendMessageLogic() {
		Consumer<EventBusResponseDTO> sendMessageResponseHandler = responseDTO -> {
			if (responseDTO.getSucceeded()) {
				LOGGER.info("[sendMessage] Successfuly Response: " + responseDTO.getBody());
			} else {
				LOGGER.error("[sendMessage] Error Response: " + responseDTO.getBody());
			}
			// Do something with the response.

		};

		for (int i = 1; i <= 3; i++) {
			sendMessage(this.processId, "Enviar mensaje número: " + i, sendMessageResponseHandler);
			
		}
	}

	/**
	 * Calls the getRegisters from the superclass in order to get Registers through
	 * the event bus.
	 */
	private void getRegistersLogic() {
		Consumer<List<RegistryDTO>> registersHandler = registerDTOs -> {
			// Do something with the response.

		};

		this.getRegisters(1L, EThingType.GATEWAY, registersHandler);
		//this.getRegisters(null, null, registersHandler);
	}

	/**
	 * Calls the getProcessById from the superclass in order to get a process by its
	 * id through the event bus.
	 */
	private void getProcessLogic() {
		Consumer<ProcessDTO> processHandler = processDTO -> {
			// Do something with the response.

		};

		this.getProcess(this.processId, processHandler);
	}

	/**
	 * Calls the getMessagesByProcessId from the superclass in order to get the
	 * messages related to a process by its id through the event bus.
	 */
	private void getMessagesByProcessIdLogic() {
		Consumer<List<MessageDTO>> messagesByProcessHandler = registerDTOs -> {
			// Do something with the response.

		};

		this.getMessagesByProcessId(this.processId, messagesByProcessHandler);
	}

}
