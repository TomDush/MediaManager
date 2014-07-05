package fr.dush.mediamanager.launcher;

import fr.dush.mediamanager.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Create and configure CDI context (Apache DeltaSpike), and start application.
 *
 * @author Thomas Duchatelle
 */
public class ContextLauncher extends Thread implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncher.class);

    private final Path configFile;
    private final Integer port;

    private ApplicationContext applicationContext;

    private boolean stopped = false;

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
        super("daemon");
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
        LOGGER.info("Medima is starting...");

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

        // Start Spring context
        new SpringApplicationBuilder(SpringConfiguration.class).properties(source)
                                                               .showBanner(false)
                                                               .listeners(this)
                                                               .run();
        LOGGER.info("Medima started.");

        // We'll arrived here only when application will be stopped.
        waitApplicationStopped();

        LOGGER.info("Medima stopped.");
    }

    private synchronized void waitApplicationStopped() {
        try {
            while (!stopped) {
                wait();
                LOGGER.debug("Waiter application stopped has been awaken. Application context={}", applicationContext);
            }

        } catch (InterruptedException e) {
            LOGGER.warn("Application has been interrupted...", e);
        }
    }

    public synchronized ApplicationContext waitApplicationStarted() {
        if (applicationContext == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                LOGGER.warn("Application has been interrupted...", e);
            }
        }

        return applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        LOGGER.debug("Receive event: {}", event);
        if (event instanceof ContextStartedEvent) {
            fireApplicationStarted(((ContextStartedEvent) event).getApplicationContext());
        } else if (event instanceof ContextRefreshedEvent) {
            fireApplicationStarted(((ContextRefreshedEvent) event).getApplicationContext());

        } else if (event instanceof ContextStoppedEvent) {
            fireApplicationStopped();
        }
    }

    private synchronized void fireApplicationStopped() {
        LOGGER.debug("Fire application stopped!");
        stopped = true;
        notifyAll();
    }

    private synchronized void fireApplicationStarted(ApplicationContext context) {
        applicationContext = context;
        notifyAll();
    }

}
