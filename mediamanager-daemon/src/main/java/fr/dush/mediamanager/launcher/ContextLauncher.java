package fr.dush.mediamanager.launcher;

import fr.dush.mediamanager.SpringConfiguration;
import fr.dush.mediamanager.remote.IStopper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Create and configure CDI context (Apache DeltaSpike), and start application.
 *
 * @author Thomas Duchatelle
 */
public class ContextLauncher extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncher.class);

    @Getter
    private Exception catchedException = null;

    private final Path configFile;
    private final Integer port;

    @Getter
    private ApplicationContext applicationContext;

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // This default handler will be replaced by ThreadExceptionsHandler when CDI Context will be created.
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Thread [{}] throw exception : {}", t.getName(), e.getMessage(), e);
            }
        });
    }

    public ContextLauncher(Path configFile, Integer port) {
        super("mediamanagerDaemon");
        setDaemon(true);

        // Config file
        this.configFile = configFile;
        this.port = port;

    }

    /** Get normalized absolute path. */
    private static String pathToString(Path configFile) {
        return configFile.toAbsolutePath().normalize().toString();
    }

    @Override
    public void run() {
        LOGGER.info("Server starting...");

        try {
            // Generic properties (provided to Spring placeholder)
            Properties source = new Properties();
            source.put("mediamanager.propertiesfile", pathToString(configFile));
            source.put("staticFiles", "remotecontrol");
            if (port != null) {
                source.put("remotecontrol.port", String.valueOf(this.port));
            }

            // Directory where Medima binaries are
            String installPath = ContextLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (installPath.matches("^/[A-Z]:/.*$")) {
                installPath = installPath.substring(1);
            }
            source.put("mediamanager.install", pathToString(Paths.get(installPath).getParent()));

            // Start SPRING context - spring is configured by annotation in SpringConfiguration class
            LOGGER.debug("Starting Spring with properties: {}", source);
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.getEnvironment()
                   .getPropertySources()
                   .addFirst(new PropertiesPropertySource("config-files", source));
            context.register(SpringConfiguration.class);
            context.refresh();

            // Wait application end...
            final IStopper stopper = fireApplicationStarted(context);

            LOGGER.info("Server started.");
            stopper.waitApplicationEnd();

            // Stopping DI Context
            LOGGER.info("Stopping server container...");
            context.stop();

            LOGGER.info("Server stopped.");

        } catch (RuntimeException e) {
            catchedException = e;
            throw e;

        } catch (Exception e) {
            catchedException = e;
            throw new RuntimeException(e);
        }
    }

    private synchronized IStopper fireApplicationStarted(AnnotationConfigApplicationContext context) {
        LOGGER.debug("Fire application is started! context={}", context);
        applicationContext = context;

        final IStopper stopper = context.getBean(IStopper.class);
        stopper.fireApplicationStarted(this);

        notifyAll();

        return stopper;
    }

}
