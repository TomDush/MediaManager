package fr.dush.mediamanager.business.configuration.producers;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Named;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Inject ModuleConfiguration in Spring beans, and register all Spring beans. */
@Named
public class ConfigurationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBeanPostProcessor.class);

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Find where to inject config
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(ModuleConfiguration.class) &&
                field.isAnnotationPresent(Config.class)) {

                Config config = field.getAnnotation(Config.class);

                LOGGER.debug("Should inject configuration into: {}", bean);
                IConfigurationManager configurationManager = getConfigurationManager();
                ModuleConfiguration moduleConfiguration = configurationManager.getModuleConfiguration(config.id());

                setValue(bean, field, moduleConfiguration);
            }
        }

        // Is this bean subscribe to events?
        if (isSubscriber(bean)) {
            LOGGER.debug("Register Spring bean {} in EventBus.", bean);
            getEventBus().register(bean);

        }
        return bean;
    }

    private boolean isSubscriber(Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if (method.getAnnotation(Subscribe.class) != null) {
                return true;
            }
        }

        return false;
    }

    private void setValue(Object bean, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(e,
                                             "Field %s.%s is not accessible",
                                             field.getDeclaringClass().getName(),
                                             field.getName());
        }
    }

    private IConfigurationManager getConfigurationManager() {
        return applicationContext.getBean(IConfigurationManager.class);
    }

    private EventBus getEventBus() {
        return applicationContext.getBean(EventBus.class);
    }

    /** Register all beans in EventBus. */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
