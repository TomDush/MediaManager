package fr.dush.mediamanager.launcher;

import fr.dush.mediamanager.SpringConfiguration;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.modulesapi.lifecycle.MediaManagerLifeCycleService;
import fr.dush.mediamanager.remote.IStopper;
import fr.dush.mediamanager.remote.impl.StopperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import static com.google.common.collect.Lists.*;

/**
 * Create and configure CDI context (Apache DeltaSpike), and start application.
 *
 * @author Thomas Duchatelle
 */
public class ContextLauncher extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncher.class);
    private Exception catchedException = null;

    private final Path configFile;
    private final int port;

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

    public ContextLauncher(Path configFile, int port) {
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

        // Find services...
        final List<MediaManagerLifeCycleService> lifeCycleServices = findLifecycleListeners();

        try {
            fireStart(lifeCycleServices);

            // Generic properties (provided to Spring placeholder)
            Properties source = new Properties();
            source.put("mediamanager.propertiesfile", pathToString(configFile));
            source.put("remotecontrol.port", String.valueOf(this.port));

            // Directory where Medima binaries are
            String installPath = ContextLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (installPath.matches("^/[A-Z]:/.*$")) {
                installPath = installPath.substring(1);
            }
            source.put("mediamanager.install", pathToString(Paths.get(installPath).getParent()));

            // Start SPRING context - spring is configured by annotation in SpringConfiguration class
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.getEnvironment()
                   .getPropertySources()
                   .addFirst(new PropertiesPropertySource("config-files", source));
            context.register(SpringConfiguration.class);
            context.refresh();

            // Wait application end...
            final IStopper stopper = context.getBean(StopperImpl.class);
            fireInitialized(stopper);
            LOGGER.info("Server started.");
            stopper.waitApplicationEnd();

            // Stopping DI Context
            LOGGER.info("Stopping server container.");
            context.stop();

            fireStop(lifeCycleServices);

            LOGGER.info("Server stopped.");

        } catch (Exception e) {
            catchedException = e;

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException(e);
        }
    }

    /** Fire event before starting DI context- not used with Spring */
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

        final ServiceLoader<MediaManagerLifeCycleService> services =
                ServiceLoader.load(MediaManagerLifeCycleService.class);
        for (Iterator<MediaManagerLifeCycleService> it = services.iterator(); it.hasNext(); ) {
            lifeCycleServices.add(it.next());
        }
        return lifeCycleServices;
    }

    private synchronized void fireInitialized(IStopper stopper) {
        stopper.fireApplicationStarted(this);

        notifyAll();
    }

    protected Exception getCatchedException() {
        return catchedException;
    }

}
