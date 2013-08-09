package fr.dush.mediamanager.engine;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediamanager.tools.CDIUtils;

/**
 * Initialize CDI context (with OpenWebBeans) to use injection in unit test.
 *
 * @author Thomas Duchatelle
 */
public class CdiJunitClassRunner extends BlockJUnit4ClassRunner {

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public CdiJunitClassRunner(Class<?> klass) throws InitializationError {
		super(klass);

		CDIUtils.bootCdiContainer();
	}

	@Override
	protected void finalize() throws Throwable {
		CDIUtils.stopCdiContainer();
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
					if (e.getCause() instanceof Error) {
						throw e.getCause();
					}

					throw e;
				}

			}
		};
	}

}