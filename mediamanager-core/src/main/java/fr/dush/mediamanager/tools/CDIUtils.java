package fr.dush.mediamanager.tools;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tools to use CDI context.
 *
 * @author Thomas Duchatelle
 *
 */
public class CDIUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CDIUtils.class);

	private static CdiContainer cdiContainer;

	private static boolean booted = false;

	public static CdiContainer getCdiContainer() {
		if (cdiContainer == null) {
			cdiContainer = CdiContainerLoader.getCdiContainer();
		}

		return cdiContainer;
	}

	public static CdiContainer bootCdiContainer() {
		final CdiContainer cdi = getCdiContainer();
		if (!booted) {
			cdi.boot();

			// Starting the application-context allows to use @ApplicationScoped beans
			ContextControl contextControl = cdiContainer.getContextControl();
			contextControl.startContext(ApplicationScoped.class);
			contextControl.startContext(Singleton.class);

			booted = true;
		}

		return cdi;
	}

	public static void stopCdiContainer() {
		if (booted) {
			getCdiContainer().shutdown();
		}
	}

	public static <T> T getBean(Class<T> javaClass) {
		if (booted) {
			return getBean(javaClass, getCdiContainer().getBeanManager());
		}

		return null; // TODO throw exception.
	}

	@SuppressWarnings("unchecked")
	private static <T> T getBean(final Class<T> javaClass, final BeanManager beanManager) {
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
}
