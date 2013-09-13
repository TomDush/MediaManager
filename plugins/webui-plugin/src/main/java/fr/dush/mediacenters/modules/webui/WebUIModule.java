package fr.dush.mediacenters.modules.webui;

import static com.google.common.collect.Lists.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

@Module(name = "WEB UI", id = "web-ui", description = "Provide full UI by web browser.")
public class WebUIModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModule.class);

	private Server server;

	@Inject
	@Configuration(definition = "configuration/webui.json")
	private ModuleConfiguration configuration;

	// TODO Add "technical":"true", to webui.json file.

	public void startServer() throws Exception {
		Properties props = new Properties();
		props.load(Resources.getResource("configuration/jetty.properties").openStream());

		server = (Server) new XmlConfiguration(readFile("WEB-INF/jetty/jetty.xml", props)).configure();
		server.setHandler((Handler) new XmlConfiguration(readFile("WEB-INF/jetty/jetty-web.xml", props)).configure());

		server.start();
	}

	private String readFile(final String fileName, Properties props) throws IOException {
		final List<String> lines = Files.readLines(new File(Resources.getResource(fileName).getFile()), Charset.forName("UTF-8"));
		while (!lines.isEmpty() && !lines.get(0).trim().startsWith("<Configure")) {
			lines.remove(0);
		}

		final String resolved = configuration.resolveProperties(Joiner.on("").join(trim(lines)), props);
		LOGGER.debug("Jetty config : {}", resolved);

		return resolved;
	}

	private List<String> trim(List<String> lines) {
		return transform(lines, new Function<String, String>() {
			@Override
			public String apply(String line) {
				return line != null ? line.trim() : line;
			}
		});
	}

	public void join() throws InterruptedException {
		server.join();
	}
}
