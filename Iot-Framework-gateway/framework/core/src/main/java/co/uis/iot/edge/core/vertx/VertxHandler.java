package co.uis.iot.edge.core.vertx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.json.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.uis.iot.edge.common.model.EventBusResponseDTO;
import co.uis.iot.edge.common.model.MessageDTO;
import co.uis.iot.edge.common.model.ProcessAliveDTO;
import co.uis.iot.edge.common.model.ProcessDTO;
import co.uis.iot.edge.common.model.RegistryDTO;
import co.uis.iot.edge.common.model.ResponseDTO;
import co.uis.iot.edge.core.exception.CommunicationException;
import co.uis.iot.edge.core.exception.ProcessDeployException;
import co.uis.iot.edge.core.exception.ProcessNotFoundException;
import co.uis.iot.edge.core.service.ICommunicationService;
import co.uis.iot.edge.core.service.IProcessService;
import co.uis.iot.edge.core.service.IRegistryService;
import co.uis.iot.edge.core.service.IStorageService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

/**
 * Vert.x methods implementation, such as event bus Bridge communication and
 * Process verticles management.
 * 
 * @author Camilo Gutiérrez
 *
 */
@Component
public class VertxHandler extends AbstractVerticle implements IVertxHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(VertxHandler.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ICommunicationService communicationService;
	
	@Autowired
	private IProcessService processService;
	
	@Autowired
	private IRegistryService registryService;
	
	@Autowired
	private IStorageService storageService;

	/**
	 * Concurrent Map of Deployed Processes (Verticles), its key is the Process Id,
	 * and its value is the Deployer Verticle (Map<processKey, VertxDeployer>).
	 */
	private static final Map<Long, VertxDeployer> deployedProcesses = new ConcurrentHashMap<>();

	@Override
	public void start() {
		initEventBusServiceListeners();
		
		final TcpEventBusBridge bridge = TcpEventBusBridge.create(vertx, new BridgeOptions()
				.addInboundPermitted(new PermittedOptions()).addOutboundPermitted(new PermittedOptions()));

		// Expose the eventbus to be accesible via Intranet
		bridge.listen(7000, res -> {
			if (res.succeeded()) {
				LOGGER.info("TCP Eventbus running on port 7000.");
			} else {
				LOGGER.info("TCP Eventbus down.");
			}
		});
	}

	@Override
	public void publishToTopic(String topic, MessageDTO messageDTO) {
		try {
			vertx.eventBus().publish(topic, new JsonObject(mapper.writeValueAsString(messageDTO)));
		} catch (JsonProcessingException e) {
			LOGGER.error("The message can't be parse to JSON.");
			throw new JsonParseException(e.getMessage(), e);
		}
	}

	@Override
	public ResponseDTO deployProcess(Long processId, String processJar, boolean deploy) {
		VertxDeployer deployer = deployedProcesses.get(processId);
		boolean deployed = false;
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (deployer == null && !deploy) {
				responseDTO = new ResponseDTO("The process wasn't deployed before.", false);
			}
			
			if (deployer != null) {
				final Future<Void> future = Future.future();
				deployer.stop(future);
				if (future.failed()) {
					throw new ProcessDeployException(future.cause());
				}
				responseDTO = new ResponseDTO("The process was undeployed successfully.", true);
				LOGGER.info("Undeployment of Process {} succeeded ({}).", processId, processJar);
			}
			if (deploy) {
				deployer = new VertxDeployer(processJar);
				final BlockingQueue<AsyncResult<String>> deployLock = new ArrayBlockingQueue<>(1);
				vertx.deployVerticle(deployer, deployLock::offer);
				final AsyncResult<String> deployResult = deployLock.take();
				if (deployResult.failed()) {
					throw new ProcessDeployException(deployResult.cause());
				}
				deployed = true;
				responseDTO = new ResponseDTO("The process was deployed successfully.", true);
				LOGGER.info("Deployment of Process {} succeeded ({}).", processId, processJar);
			}
		} catch (Exception e) {
			responseDTO = new ResponseDTO(e.getMessage(), false);
			LOGGER.error("Deployment of Process {} failed.", processId, e);
		} finally {
			try {
				processService.updateProcessDeployStatus(processId, deployed);
			} catch (ProcessNotFoundException e) {
				// ignore as it's thrown when the item was deleted.
			}
			if (deployed) {
				deployedProcesses.put(processId, deployer);
			} else {
				deployedProcesses.remove(processId);
			}
		}
		return responseDTO;
	}
	
	@Override
	public ProcessAliveDTO checkProcessAlive(String topic, String message) {
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		vertx.eventBus().send(topic, new JsonObject(message), response -> {
			if (response.succeeded()) {
				completableFuture.complete(response.result().body().toString());
			} else {
				completableFuture.complete("{\r\n" + 
						"  \"isAlive\": \"false\",\r\n" + 
						"  \"message\": \"" + response.cause().getMessage() + "\"\r\n" + 
						"}");
			}
		});

		try {
			return mapper.readValue(completableFuture.get(), ProcessAliveDTO.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Initializes the consumers to exposes services.
	 */
	private void initEventBusServiceListeners() {
		vertx.eventBus().consumer("sendMessage", this::sendMessageToExternalBroker);
		vertx.eventBus().consumer("getRegistries", this::getRegistriesFromDatabase);
		vertx.eventBus().consumer("getProcessById", this::getProcessByIdFromDatabase);
		vertx.eventBus().consumer("getMessagesByProcessId", this::getMessagesByProcessId);
		vertx.eventBus().consumer("saveMessage", this::saveMessage);
	}
	
	/**
	 * Parses the received message to MessageDTO and processes it through ComunicationService.
	 * 
	 * @param message. 
	 */
	private void sendMessageToExternalBroker(final Message<Object> message) {
		try {
			LOGGER.info("sendMessage method called with message: {}", message.body());
			MessageDTO messageDTO = mapper.readValue(message.body().toString(), MessageDTO.class);
			communicationService.sendActiveMQMessage(messageDTO);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(true, "The message was processed successfully")));
		} catch (CommunicationException e) {
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, e.getMessage())));
		} catch (IOException e) {
			LOGGER.error("The received json message couldn't be parsed to MessageDTO {}", e);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The received json message couldn't be parsed to MessageDTO")));
		} 
	}
	
	/**
	 * Returns the list of {@link RegistryDTO} which matches with the request body.
	 * 
	 * @param message
	 */
	private void getRegistriesFromDatabase(final Message<Object> message) {
		try {
			LOGGER.info("getRegistries method called with message: {}", message.body());
			RegistryDTO registryDTO = mapper.readValue(message.body().toString(), RegistryDTO.class);
			List<RegistryDTO> registryDTOs = new ArrayList<>();
			if (registryDTO.getId() == null) {
				registryDTOs = registryService.getRegistries(registryDTO.getType());
			} else {
				if(registryDTO.getType() == null) {
					message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "To find by id the device type should also be specified.")));
					return;
				}
			
				registryDTOs = Collections.singletonList(registryService.getRegistriesByIdBackendAndType(registryDTO.getType(), registryDTO.getId()));
			}
	
			message.reply(new JsonObject(getEventBusResponseDTOAsString(true, registryDTOs)));
		} catch (IOException e) {
			LOGGER.error("The received json message couldn't be parsed to RegistryDTO {}", e);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The received json message couldn't be parsed to RegistryDTO")));
		} 
	}
	
	/**
	 * Parses the received message to ProcessDTO and searches for the requested process in the DB.
	 * 
	 * @param message. 
	 */
	private void getProcessByIdFromDatabase(final Message<Object> message) {
		try {
			LOGGER.info("getProcessById method called with message: {}", message.body());
			ProcessDTO processDTO = mapper.readValue(message.body().toString(), ProcessDTO.class);
			if (processDTO.getId() == null) {
				message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The process' id can't be null or empty.")));
				return;
			}
			ProcessDTO dbProcessDTO = processService.getProcessByIdBackend(processDTO.getId());
			message.reply(new JsonObject(getEventBusResponseDTOAsString(true, dbProcessDTO)));
		} catch (ProcessNotFoundException e) {
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, e.getMessage())));
		} catch (IOException e) {
			LOGGER.error("The received json message couldn't be parsed to MessageDTO {}", e);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The received json message couldn't be parsed to MessageDTO")));
		} 
	}
	
	/**
	 * Parses the received message to ProcessDTO and searches for the requested process' messages in the DB.
	 * 
	 * @param message. 
	 */
	private void getMessagesByProcessId(final Message<Object> message) {
		try {
			LOGGER.info("getMessagesByProcessId method called with message: {}", message.body());
			ProcessDTO processDTO = mapper.readValue(message.body().toString(), ProcessDTO.class);
			if (processDTO.getId() == null) {
				message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The process' id can't be null or empty.")));
				return;
			}
			List<MessageDTO> messagesDTO = storageService.getMessagesByProcessId(processDTO.getId());
			message.reply(new JsonObject(getEventBusResponseDTOAsString(true, messagesDTO)));
		} catch (ProcessNotFoundException e) {
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, e.getMessage())));
		} catch (IOException e) {
			LOGGER.error("The received json message couldn't be parsed to MessageDTO {}", e);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The received json message couldn't be parsed to MessageDTO")));
		} 
	}
	
	/**
	 * Parses the received message to MessageDTO and stores it in the DB.
	 * Note: This message will be deleted automatically depending on the db_cleanup_time property if this was configured in the gateway.
	 * 
	 * @param message. 
	 */
	private void saveMessage(final Message<Object> message) {
		try {
			LOGGER.info("saveMessage method called with message: {}", message.body());
			MessageDTO messageDTO = mapper.readValue(message.body().toString(), MessageDTO.class);
			if (messageDTO.getProcessId() == null) {
				message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The process' id can't be null or empty.")));
				return;
			}
			storageService.saveMessage(messageDTO, true);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(true, "The message was stored succesfully.")));
		} catch (ProcessNotFoundException e) {
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, e.getMessage())));
		} catch (IOException e) {
			LOGGER.error("The received json message couldn't be parsed to MessageDTO {}", e);
			message.reply(new JsonObject(getEventBusResponseDTOAsString(false, "The received json message couldn't be parsed to MessageDTO")));
		} 
	}
	
	/**
	 * Returns the {@link EventBusResponseDTO} as a String.
	 * 
	 * @param succeeded
	 * @param body
	 * @return
	 */
	private String getEventBusResponseDTOAsString(boolean succeeded, Object body) {
		try {
			return mapper.writeValueAsString(new EventBusResponseDTO(succeeded, mapper.writeValueAsString(body)));
		} catch (JsonProcessingException e) {
			LOGGER.error("The json message couldn't be parsed {}", e);
			return getEventBusResponseDTOAsString(false, "The json message couldn't be parsed");
		}
	}
}
