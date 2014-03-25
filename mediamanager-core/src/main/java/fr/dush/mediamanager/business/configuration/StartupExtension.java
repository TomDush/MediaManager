package fr.dush.mediamanager.business.configuration;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.events.lifecycle.ApplicationStarted;
import fr.dush.mediamanager.tools.CDIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class StartupExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupExtension.class);

    private final List<Class<?>> startupBeans = newArrayList();

    <X> void processBean(@Observes ProcessBean<X> event) {
        if (event.getAnnotated().isAnnotationPresent(ApplicationScoped.class) &&
            (event.getAnnotated().isAnnotationPresent(Startup.class) ||
             event.getAnnotated().isAnnotationPresent(Module.class))) {

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
        LOGGER.debug("Initialize beans : {}", startupBeans);
        for (Class<?> clazz : startupBeans) {
            try {
                // the call to toString() is a cheat to force the bean to be initialized
                CDIUtils.getBean(clazz).toString();

            } catch (Exception e) {
                LOGGER.error("Can't initialise bean of type '{}' : {}", clazz.getName(), e.getMessage(), e);
            }
        }

        LOGGER.info("All beans has been initilized : {}", startupBeans);
    }
}
