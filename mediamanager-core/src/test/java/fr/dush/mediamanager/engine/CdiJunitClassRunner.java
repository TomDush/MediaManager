package fr.dush.mediamanager.engine;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
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
		lifecycle.startApplication(null);
	}

	@Override
	protected void finalize() throws Throwable {
		lifecycle.stopApplication(null);
		super.finalize();
	}

	@Override
	protected Object createTest() throws Exception {

		return getBean(getTestClass().getJavaClass());
	}

	@SuppressWarnings("unchecked")
	protected <T> T getBean(final Class<T> javaClass) {
		final BeanManager beanManager = lifecycle.getBeanManager();
		final Set<Bean<?>> beans = beanManager.getBeans(javaClass, new Any() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Any.class;
			}
		});

		if (beans.isEmpty()) {
			LOGGER.error("No CDI bean found for class {}.", javaClass);
			throw new RuntimeException("No CDI bean found for class " + javaClass);
		}

		final Bean<?> bean = beans.iterator().next();
		return (T) beanManager.getReference(bean, javaClass, beanManager.createCreationalContext(bean));
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
					if (e.getCause() instanceof Error) {
						throw e.getCause();
					}

					throw e;
				}

			}
		};
	}

}