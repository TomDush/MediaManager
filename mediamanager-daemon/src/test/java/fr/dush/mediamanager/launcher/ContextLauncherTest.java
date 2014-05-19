package fr.dush.mediamanager.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.remote.Stopper;

@RunWith(BlockJUnit4ClassRunner.class)
public class ContextLauncherTest implements UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextLauncherTest.class);

    private static final int DEFAULT_JUNIT_PORT = 3536;

    private Throwable catchedException = null;

    private Path configFile = Paths.get("../mediamanager-core/src/test/resources/dbconfig-junit.properties");

    @Test
    public void testCreateContextAndStop() throws Exception {

        ContextLauncher launcher = new ContextLauncher(configFile, DEFAULT_JUNIT_PORT);
        launcher.setUncaughtExceptionHandler(this);
        synchronized (launcher) {
            launcher.start();

            // Wait is started...
            launcher.wait(5000);
        }

        // TODO Stop application
        final Stopper stopper = null;//CDIUtils.getBean(Stopper.class);
        LOGGER.info("Try to stop application with stopper {}", stopper);
        stopper.stopApplication();

        LOGGER.info("Joining ...");
        launcher.join(1000);

        assertThat(launcher.isAlive()).as("Launcher alive").isFalse();

        if (catchedException != null && catchedException instanceof Exception) {
            throw (Exception) catchedException;
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Error in in thread {}", t, e);
        catchedException = e;
    }
}
