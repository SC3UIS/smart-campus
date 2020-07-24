package co.uis.iot.edge.common.model;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Contains the basic attributes and methods to operate through the event bus
 * between the Framework and the processes.
 * 
 * @author Camilo Gutiérrez
 *
 */
public abstract class ProcessVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessVerticle.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	protected Long processId;
	protected String processTopic;

	public ProcessVerticle(Long processId, String processTopic) {
		super();
		this.processId = processId;
		this.processTopic = processTopic;
	}

	/**
	 * Sends the passed message to the gateway framework through the event bus and
	 * return the answer to the passed function.
	 * 
	 * @param processId       - process' id related to the message.
	 * @param payload         - message to be sent.
	 * @param responseHandler - function to handler the response.
	 */
	public void sendMessage(Long processId, String payload, Consumer<EventBusResponseDTO> responseHandler) {
		try {
			MessageDTO messageDTO = new MessageDTO(null, processId, payload, null);
			this.getVertx().eventBus().send("sendMessage", mapper.writeValueAsString(messageDTO), response -> {
				try {
					responseHandler
							.accept(mapper.readValue(response.result().body().toString(), EventBusResponseDTO.class));
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Gets the registers (Gateway, Sensor, Actuator) from the framework through the
	 * event bus and return the answer to the passed function.
	 * 
	 * If the id is null then the complete list of registers will be return. Returns
	 * null if there was a problem with the request.
	 * 
	 * @param id              - registry's id.
	 * @param thingType       - registry's type.
	 * @param responseHandler - function to handler the response.
	 */
	public void getRegisters(Long id, EThingType thingType, Consumer<List<RegistryDTO>> responseHandler) {
		try {
			RegistryDTO registryDTO = new RegistryDTO(id, null, null, null, thingType);
			vertx.eventBus().send("getRegistries", mapper.writeValueAsString(registryDTO), response -> {
				if (response.result().body() != null) {
					try {
						EventBusResponseDTO responseDTO = mapper.readValue(response.result().body().toString(),
								EventBusResponseDTO.class);
						if (responseDTO.getSucceeded()) {
							LOGGER.info("[getRegistries] Successfuly Response: " + responseDTO.getBody());
							responseHandler.accept(
									mapper.readValue(responseDTO.getBody(), new TypeReference<List<RegistryDTO>>() {
									}));
						} else {
							LOGGER.error("[getRegistries] Error Response: " + responseDTO.getBody());
							responseHandler.accept(null);
						}
					} catch (IOException e) {
						LOGGER.error(e);
					}
				}
			});
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Gets a process by its id from the framework through the event bus and return
	 * the answer to the passed function.
	 * 
	 * 
	 * @param id              - process' id.
	 * @param responseHandler - function to handler the response.
	 */
	public void getProcess(Long id, Consumer<ProcessDTO> responseHandler) {
		try {
			ProcessDTO processDTO = new ProcessDTO(id, null, null, false, null);
			vertx.eventBus().send("getProcessById", mapper.writeValueAsString(processDTO), response -> {
				if (response.result().body() != null) {
					try {
						EventBusResponseDTO responseDTO = mapper.readValue(response.result().body().toString(),
								EventBusResponseDTO.class);
						if (responseDTO.getSucceeded()) {
							LOGGER.info("[getProcess] Successfuly Response: " + responseDTO.getBody());
							responseHandler.accept(mapper.readValue(responseDTO.getBody(), ProcessDTO.class));
						} else {
							LOGGER.error("[getProcess] Error Response: " + responseDTO.getBody());
						}
					} catch (IOException e) {
						LOGGER.error(e);
					}
				}
			});
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Subscribes to the process topic in order to receive the messages sent from
	 * the framework.
	 * 
	 * @param processTopic - Process' topic.
	 */
	private void consumeMessages() {
		LOGGER.info("[consumeMessages] Waiting for messages in the topic: " + this.processTopic);
		this.getVertx().eventBus().consumer(this.processTopic, msg -> {
			try {
				LOGGER.info("[consumeMessages] " + msg.body().toString());
				messageHandler(mapper.readValue(msg.body().toString(), MessageDTO.class));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	/**
	 * Keeps listening if the framework ask for the status of the process.
	 */
	private void keepAliveListener() {
		LOGGER.info("[keepAliveListener] Waiting for keepalive request for the process: " + this.processId);
		String keepAliveTopic = "keepAlive/" + this.processId;
		this.getVertx().eventBus().consumer(keepAliveTopic, msg -> {
			try {
				LOGGER.info(
						"[keepAliveListener] KeepAlive received on process id: " + this.processId + ", procesando...");
				msg.reply(new JsonObject(mapper.writeValueAsString(processKeepAlive())));
			} catch (JsonProcessingException e) {
				LOGGER.error(e);
			}
		});
	}

	/**
	 * Saves the passed message in the framework DB through the event bus and return
	 * the answer to the passed function.
	 * 
	 * @param processId       - process' id related to the message.
	 * @param payload         - message to be sent.
	 * @param responseHandler - function to handler the response.
	 */
	public void saveMessage(Long processId, String payload, Consumer<EventBusResponseDTO> responseHandler) {
		try {
			MessageDTO messageDTO = new MessageDTO(null, processId, payload, null);
			this.getVertx().eventBus().send("saveMessage", mapper.writeValueAsString(messageDTO), response -> {
				try {
					responseHandler
							.accept(mapper.readValue(response.result().body().toString(), EventBusResponseDTO.class));
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Gets the messages of a process by its id event bus and return the answer to
	 * the passed function.
	 * 
	 * The id is required. Returns null if there was a problem with the request.
	 * 
	 * @param id              - process' id.
	 * @param responseHandler - function to handler the response.
	 */
	public void getMessagesByProcessId(Long id, Consumer<List<MessageDTO>> responseHandler) {
		try {
			ProcessDTO processDTO = new ProcessDTO(id, null, null, false, null);
			this.getVertx().eventBus().send("getMessagesByProcessId", mapper.writeValueAsString(processDTO),
					response -> {
						if (response.result().body() != null) {
							try {
								EventBusResponseDTO responseDTO = mapper.readValue(response.result().body().toString(),
										EventBusResponseDTO.class);
								if (responseDTO.getSucceeded()) {
									LOGGER.info(
											"[getMessagesByProcessId] Successfuly Response: " + responseDTO.getBody());
									responseHandler.accept(mapper.readValue(responseDTO.getBody(),
											new TypeReference<List<MessageDTO>>() {
											}));
								} else {
									LOGGER.error("[getMessagesByProcessId] Error Response: " + responseDTO.getBody());
									responseHandler.accept(null);
								}
							} catch (IOException e) {
								LOGGER.error(e);
							}
						}
					});
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * This method has to be called from the subclass in order to works properly
	 * with the framework.
	 */
	public void initConsumers() {
		this.consumeMessages();
		this.keepAliveListener();

	}

	/**
	 * The user has to implement the logic of this method to indicate if the process
	 * is working or not.
	 * 
	 * @return a {@link ProcessAliveDTO} which indicates the process' status.
	 */
	/**
	 * @return
	 */
	public abstract ProcessAliveDTO processKeepAlive();

	/**
	 * The user has to implement the logic of this method to handle the received
	 * messages from the framework.
	 * 
	 * @param messageDTO - Received message.
	 */
	public abstract void messageHandler(MessageDTO messageDTO);

}
