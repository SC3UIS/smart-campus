package co.uis.iot.edge.core.vertx;

import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Verticle;

/**
 * {@link Verticle} used to deploy Processes Verticles, required so this
 * instance is stopped to stop the deployed process.
 * 
 * @author Camilo Gutierrez
 *
 */
public class VertxDeployer extends AbstractVerticle {

	private String processJar;

	private String processDeployId;

	/**
	 * Creates an instances of {@link VertxDeployer}
	 * 
	 * @param processJar PROCESS_JAR property for the given Process to be deployed.
	 */
	public VertxDeployer(String processJar) {
		this.processJar = processJar;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.deployVerticle(processJar, result -> {
			if (result.succeeded()) {
				processDeployId = result.result();
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		final Set<String> deployedProcesses = vertx.deploymentIDs();
		if (deployedProcesses.contains(processDeployId)) {
			vertx.undeploy(processDeployId, result -> {
				if (result.succeeded()) {
					stopFuture.complete();
				} else {
					stopFuture.fail(result.cause());
				}
			});
		} else {
			stopFuture.fail("Process " + processJar + "wasn't found when trying to undeploy it.");
		}
	}

	@Override
	public String toString() {
		return "VertxDeployer [" + processDeployId + ": " + processJar + "]";
	}

}
