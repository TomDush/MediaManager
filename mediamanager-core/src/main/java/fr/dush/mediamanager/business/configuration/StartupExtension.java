package fr.dush.mediamanager.business.configuration;

import static com.google.common.collect.Lists.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.events.lifecycle.ApplicationStarted;
import fr.dush.mediamanager.tools.CDIUtils;

public class StartupExtension implements Extension {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupExtension.class);

	private final List<Class<?>> startupBeans = newArrayList();

	<X> void processBean(@Observes ProcessBean<X> event) {
		if (event.getAnnotated().isAnnotationPresent(ApplicationScoped.class)
				&& (event.getAnnotated().isAnnotationPresent(Startup.class) || event.getAnnotated().isAnnotationPresent(Module.class))) {

			Class<?> beanClass = event.getBean().getBeanClass();
			if (event.getAnnotated().isAnnotationPresent(Startup.class)) {
				final Class<?> superclass = event.getAnnotated().getAnnotation(Startup.class).superclass();
				if (!Startup.class.equals(superclass)) {
					beanClass = superclass;
				}
			}

			startupBeans.add(beanClass);
		}
	}

	void afterDeploymentValidation(@Observes ApplicationStarted event) {
		for (Class<?> clazz : startupBeans) {
			// the call to toString() is a cheat to force the bean to be initialized
			CDIUtils.getBean(clazz).toString();
		}

		LOGGER.debug("All beans has been initilized.");
	}
}
