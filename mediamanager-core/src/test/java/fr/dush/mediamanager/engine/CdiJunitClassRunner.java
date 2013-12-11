package fr.dush.mediamanager.engine;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediamanager.tools.CDIUtils;

/**
 * Initialize CDI context to use injection in unit test.
 * <p> Never stop CDI container... It's not a good idea to close it because it could be stopped while another worker work use it ! </p>
 *
 * @author Thomas Duchatelle
 */
public class CdiJunitClassRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdiJunitClassRunner.class);

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        System.setProperty("mediamanager.propertiesfile", "src/test/resources/dbconfig-junit.properties");
    }

    public CdiJunitClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        LOGGER.debug("Booting CDI container for '{}'", this.hashCode());
        CDIUtils.bootCdiContainer();
    }


    @Override
    protected Object createTest() throws Exception {
        return CDIUtils.getBean(getTestClass().getJavaClass());
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        final Statement st = super.methodInvoker(method, test);

        // Recast InvocatonTargetException
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    st.evaluate();
                } catch (Exception e) {
                    LOGGER.debug("Statement fail with error ", e);
                    if (e.getCause() instanceof Error) {
                        throw e.getCause();
                    }

                    throw e;
                }
            }
        };
    }
}