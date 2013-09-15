package fr.dush.mediamanager.launcher;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediacenters.modules.enrich.moviesdb.TheMovieDBProvider;
import fr.dush.mediacenters.modules.webui.WebUIModule;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.business.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import fr.dush.mediamanager.remote.Stopper;
import fr.dush.mediamanager.tools.CDIUtils;

/**
 * Create and configure CDI context (Apache DeltaSpike), and start application.
 *
 * @author Thomas Duchatelle
 *
 */
public class ContextLauncher extends Thread {

	private static final int REGISTRY_PORT = 1099;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncher.class);

	private Exception catchedException = null;

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public ContextLauncher(Path configFile, int port) {
		super("mediamanagerDaemon");
		setDaemon(true);

		// TODO Do something with params (configFile and port)

		final URL installPath = ContextLauncher.class.getProtectionDomain().getCodeSource().getLocation();
		System.setProperty("mediamanager.install", pathToString(Paths.get(installPath.getPath()).getParent()));
		if (configFile != null) {
			System.setProperty("mediamanager.propertiesfile", pathToString(configFile));
		}
	}

	/** Get normalized absolute path. */
	private static String pathToString(Path configFile) {
		return configFile.toAbsolutePath().normalize().toString();
	}

	@Override
	public void run() {
		try {
			CDIUtils.bootCdiContainer();

			// Register remote interface
			startRegistry();

			final MediaManagerRMI remoteInterface = CDIUtils.getBean(MediaManagerRMI.class);
			Naming.rebind("rmi://localhost/" + MediaManagerRMI.class.getSimpleName(), remoteInterface);

			// Wait application end...
			fireInitialized();
			final Stopper stopper = CDIUtils.getBean(Stopper.class);
			LOGGER.info("Server started.");
			stopper.waitApplicationEnd();

			// Stopping CDI
			LOGGER.info("Stopping server container.");
			CDIUtils.stopCdiContainer();

		} catch (RuntimeException e) {
			catchedException = e;
			throw e;

		} catch (Exception e) {
			catchedException = e;
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create dynamic registry, if isn't started...
	 *
	 * @throws RemoteException
	 */
	private Registry startRegistry() throws RemoteException {
		return LocateRegistry.createRegistry(REGISTRY_PORT);
	}

	private synchronized void fireInitialized() {
		// TODO force application scoped "Startup" beans to be initialized.
		CDIUtils.getBean(WebUIModule.class).toString();
		CDIUtils.getBean(IMovieDAO.class).toString();
		CDIUtils.getBean(TheMovieDBProvider.class).toString();
		CDIUtils.getBean(IArtDownloader.class).toString();
		CDIUtils.getBean(MoviesScanner.class).toString();

		notifyAll();
	}

	protected Exception getCatchedException() {
		return catchedException;
	}

}
