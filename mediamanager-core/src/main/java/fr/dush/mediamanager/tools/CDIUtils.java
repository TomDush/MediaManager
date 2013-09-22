package fr.dush.mediamanager.tools;

import static com.google.common.collect.Lists.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
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

	private static BeanManager beanManager;

	/** If started by Java SE */
	private static CdiContainer cdiContainer;

	private static boolean booted = false;

	public static BeanManager bootCdiContainer() {
		if (!booted) {
			try {
				// Try to get existing Java EE CDI Container
				final CDI<Object> weld = CDI.current();
				beanManager = weld.getBeanManager();

				LOGGER.debug("Using existing CDI Container : {}", weld);
				booted = true;

			} catch (IllegalStateException e) {
				LOGGER.debug("Create Java SE CDI container.");

				// Start Java SE CDI Container
				cdiContainer = CdiContainerLoader.getCdiContainer();
				cdiContainer.boot();

				// Starting the application-context allows to use @ApplicationScoped beans
				ContextControl contextControl = cdiContainer.getContextControl();
				contextControl.startContext(ApplicationScoped.class);
				contextControl.startContext(Singleton.class);

				beanManager = cdiContainer.getBeanManager();

				booted = true;
			}

		} else {
			LOGGER.warn("CDI is already started.");
		}

		return beanManager;
	}

	public static void stopCdiContainer() {
		if (booted && cdiContainer != null) {
			LOGGER.debug("Stopping CDI Container...");
			cdiContainer.shutdown();

			cdiContainer = null;
			beanManager = null;
			booted = false;
		}
	}

	public static <T> T getBean(Class<T> javaClass) {
		if (booted) {
			return getBean(beanManager, javaClass);
		}

		throw new IllegalStateException("CDI Container isn't booted.");
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(final BeanManager beanManager, final Class<T> javaClass) {
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

	@SuppressWarnings("unchecked")
	public static <T> List<T> getBeans(final Class<T> javaClass) {
		final BeanManager beanManager = CdiContainerLoader.getCdiContainer().getBeanManager();

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

		List<T> list = newArrayList();
		for (Bean<?> bean : beans) {
			list.add((T) beanManager.getReference(bean, javaClass, beanManager.createCreationalContext(bean)));
		}

		return list;
	}
}
