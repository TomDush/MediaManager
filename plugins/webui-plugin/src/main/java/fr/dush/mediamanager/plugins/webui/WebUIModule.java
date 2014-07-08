package fr.dush.mediamanager.plugins.webui;

import com.google.common.io.Resources;
import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

/** This service start a embedded jetty */
@Service
public class WebUIModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModule.class);

    @Config(id = "webui")
    private ModuleConfiguration configuration;

    private Server server;

    /** Start Jetty, and CDI container, before Daemon do it. */
    @PostConstruct
    public void startJetty() throws ModuleLoadingException {
        try {
            startServer();
        } catch (Exception e) {
            LOGGER.error("Can't start jetty server: {}", e.getMessage(), e);
            throw new ModuleLoadingException("Can't start Jetty server for WEB-UI plugin.", e);
        }
    }

    @PreDestroy
    public void stopJetty() {
        stopServer();
    }

    /** Waiting for server stop ... */
    public void join() throws InterruptedException {
        server.join();
    }

    private void startServer() throws Exception {
        server = (Server) new XmlConfiguration(readFile("jetty/jetty.xml")).configure();
        server.setHandler((Handler) new XmlConfiguration(readFile("jetty/jetty-web.xml")).configure());

        server.start();

        LOGGER.debug("Jetty server started...");
    }

    /** Stopping server (and CDI context) ... */
    private void stopServer() {
        if (server == null) {
            return;
        }

        LOGGER.info("Stopping webui server...");
        try {
            server.stop();
            server.join();

        } catch (Exception e) {
            LOGGER.warn("Can't stop WebUI server : {}", e.getMessage(), e);
        }
    }

    private String readFile(final String fileName) throws IOException {

        final String content = readResource(Resources.getResource(fileName));

        // Use core config resolver to replace placeholder by real value.
        final String resolved = configuration.resolveProperties(content, new Properties());
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
