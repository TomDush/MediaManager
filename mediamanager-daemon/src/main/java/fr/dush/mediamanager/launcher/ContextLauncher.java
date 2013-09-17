package fr.dush.mediamanager.launcher;


import static com.google.common.collect.Lists.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.modulesapi.lifecycle.MediaManagerLifeCycleService;
import fr.dush.mediamanager.remote.Stopper;
import fr.dush.mediamanager.tools.CDIUtils;

/**
 * Create and configure CDI context (Apache DeltaSpike), and start application.
 *
 * @author Thomas Duchatelle
 *
 */
public class ContextLauncher extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncher.class);

	private Exception catchedException = null;

	private Path configFile = null;

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public ContextLauncher(Path configFile, int port) {
		super("mediamanagerDaemon");
		setDaemon(true);
		this.configFile = configFile;

		String installPath = ContextLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (installPath.matches("^/[A-Z]:/.*$")) {
			installPath = installPath.substring(1);
		}
		System.setProperty("mediamanager.install", pathToString(Paths.get(installPath).getParent()));
		if (configFile != null) {
			System.setProperty("mediamanager.propertiesfile", pathToString(configFile));
		}
		System.setProperty("mediamanager.port", String.valueOf(port));
	}

	/** Get normalized absolute path. */
	private static String pathToString(Path configFile) {
		return configFile.toAbsolutePath().normalize().toString();
	}

	@Override
	public void run() {

		// Find services...
		final List<MediaManagerLifeCycleService> lifeCycleServices = findLifecycleListeners();

		try {
			fireStart(lifeCycleServices);
			CDIUtils.bootCdiContainer();

			// Wait application end...
			final Stopper stopper = CDIUtils.getBean(Stopper.class);
			fireInitialized(stopper);
			LOGGER.info("Server started.");
			stopper.waitApplicationEnd();

			// Stopping CDI
			LOGGER.info("Stopping server container.");
			CDIUtils.stopCdiContainer();

			fireStop(lifeCycleServices);

		} catch (RuntimeException e) {
			catchedException = e;
			throw e;

		} catch (Exception e) {
			catchedException = e;
			throw new RuntimeException(e);
		}
	}

	private void fireStart(List<MediaManagerLifeCycleService> lifeCycleServices) throws ModuleLoadingException {
		for (MediaManagerLifeCycleService serv : lifeCycleServices) {
			serv.beforeStartCdi(configFile);
		}
	}

	private void fireStop(List<MediaManagerLifeCycleService> lifeCycleServices) {
		for (MediaManagerLifeCycleService serv : lifeCycleServices) {
			serv.afterStopCdi();
		}
	}

	private List<MediaManagerLifeCycleService> findLifecycleListeners() {
		List<MediaManagerLifeCycleService> lifeCycleServices = newArrayList();

		final ServiceLoader<MediaManagerLifeCycleService> services = ServiceLoader.load(MediaManagerLifeCycleService.class);
		for (Iterator<MediaManagerLifeCycleService> it = services.iterator(); it.hasNext();) {
			lifeCycleServices.add(it.next());
		}
		return lifeCycleServices;
	}

	private synchronized void fireInitialized(Stopper stopper) {
		stopper.fireApplciationStarted(this);

		notifyAll();
	}

	protected Exception getCatchedException() {
		return catchedException;
	}

}
