package fr.dush.mediacenters.modules.webui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.modulesapi.lifecycle.MediaManagerLifeCycleService;

public class WebUIModule implements MediaManagerLifeCycleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModule.class);

	private Server server;

	/** Start Jetty, and CDI container, before Daemon do it. */
	@Override
	public void beforeStartCdi(Path configFilePath) throws ModuleLoadingException {

		try {
			Properties props = new Properties();
			props.load(Resources.getResource("configuration/jetty.properties").openStream());
			if (configFilePath != null && configFilePath.toFile().exists()) {
				props.load(new FileInputStream(configFilePath.toFile()));
			}

			startServer(props);

		} catch (Exception e) {
			LOGGER.error("Can't start Jetty server", e);
			throw new ModuleLoadingException("Can't start jetty server.", e);
		}
	}

	@Override
	public void afterStopCdi() {
		stopServer();
	}

	/** Waiting for server stop ... */
	public void join() throws InterruptedException {
		server.join();
	}

	private void startServer(Properties props) throws Exception {
		server = (Server) new XmlConfiguration(readFile("jetty/jetty.xml", props)).configure();
		server.setHandler((Handler) new XmlConfiguration(readFile("jetty/jetty-web.xml", props)).configure());

		server.start();

		LOGGER.debug("Jetty server started...");
	}

	/** Stopping server (and CDI context) ... */
	private void stopServer() {
		if (server == null) return;

		LOGGER.info("Stopping webui server...");
		try {
			server.stop();
			server.join();

		} catch (Exception e) {
			LOGGER.warn("Can't stop WebUI server : {}", e.getMessage(), e);
		}
	}

	private String readFile(final String fileName, Properties props) throws IOException {

		final String content = readResource(Resources.getResource(fileName));

		// Use core resolver with values found in system and in properties file.
		ModuleConfiguration conf = new ModuleConfiguration(null, new FieldSet("jetty"));

		final String resolved = conf.resolveProperties(content, props);
		LOGGER.debug("Jetty config : {}", resolved);

		// final String resolved = content;
		return resolved;
	}

	private String readResource(URL resource) throws IOException {
		StringBuilder sb = new StringBuilder();

		try (final InputStream stream = resource.openStream()) {
			try (final Scanner scanner = new Scanner(stream).useDelimiter("\\n")) {
				boolean content = false;
				while (scanner.hasNext()) {
					final String s = scanner.next().trim();
					if (content || s.startsWith("<Configure")) {
						content = true;
						sb.append(s);
					}
				}
			}
		}

		return sb.toString();
	}
}
