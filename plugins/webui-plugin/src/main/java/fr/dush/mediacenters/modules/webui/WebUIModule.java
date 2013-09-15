package fr.dush.mediacenters.modules.webui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

@ApplicationScoped
@Module(name = "WEB UI", id = "web-ui", description = "Provide full UI by web browser.")
public class WebUIModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModule.class);

	private Server server;

	@Inject
	@Configuration(definition = "configuration/webui.json")
	private ModuleConfiguration configuration;

	@PostConstruct
	public void startServer() throws Exception {
		Properties props = new Properties();
		props.load(Resources.getResource("configuration/jetty.properties").openStream());

		server = (Server) new XmlConfiguration(readFile("jetty/jetty.xml", props)).configure();
		server.setHandler((Handler) new XmlConfiguration(readFile("jetty/jetty-web.xml", props)).configure());

		server.start();
	}

	@PreDestroy
	private void stopServer() {
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

		final String resolved = configuration.resolveProperties(content, props);
		LOGGER.debug("Jetty config : {}", resolved);

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

	/** Waiting for server stop ... */
	public void join() throws InterruptedException {
		server.join();
	}
}
