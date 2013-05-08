package fr.dush.mediamanager.engine;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Initialize CDI context (with OpenWebBeans) to use injection in unit test.
 *
 * @author Thomas Duchatelle
 */
public class CdiJunitClassRunner extends BlockJUnit4ClassRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(CdiJunitClassRunner.class);

	private final ContainerLifecycle lifecycle;

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public CdiJunitClassRunner(Class<?> klass) throws InitializationError {
		super(klass);

		lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
	}

	@Override
	protected Object createTest() throws Exception {
		final BeanManager beanManager = lifecycle.getBeanManager();

		final Set<Bean<?>> beans = beanManager.getBeans(getTestClass().getJavaClass());

		if (beans.isEmpty()) {
			LOGGER.error("No CDI bean found for class {}.", getTestClass().getJavaClass());
			throw new RuntimeException("No test method found for class " + getTestClass().getJavaClass());
		}

		final Bean<?> bean = beans.iterator().next();
		return beanManager.getReference(bean, getTestClass().getJavaClass(), beanManager.createCreationalContext(bean));
	}

	@Override
	public void run(RunNotifier notifier) {
		lifecycle.startApplication(null);

		super.run(notifier);

		lifecycle.stopApplication(null);
	}

}
