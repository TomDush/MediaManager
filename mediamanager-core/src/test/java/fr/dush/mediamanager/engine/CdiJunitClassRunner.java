package fr.dush.mediamanager.engine;

import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Initialize CDI context to use injection in unit test. <p> Never stop CDI container... It's not a good idea to close
 * it because it could be stopped while another worker work use it ! </p>
 *
 * @author Thomas Duchatelle
 */
// TODO Add Junit configuration
public class CdiJunitClassRunner extends SpringJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdiJunitClassRunner.class);

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        System.setProperty("mediamanager.propertiesfile", "src/test/resources/dbconfig-junit.properties");
    }

    public CdiJunitClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

}