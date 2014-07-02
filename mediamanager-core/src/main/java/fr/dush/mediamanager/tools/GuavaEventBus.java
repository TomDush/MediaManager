package fr.dush.mediamanager.tools;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provide a Guava Event Bus to all Spring beans. Spring beans are registered in event bus automatically.
 *
 * @see fr.dush.mediamanager.business.configuration.producers.ConfigurationBeanPostProcessor
 */
@Configuration
public class GuavaEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaEventBus.class);

    private static final EventBus eventBus = new EventBus();

    @Bean(name = "eventBus")
    public EventBus createGuavaEventBus() {
        return eventBus;
    }

    public static void unregister(Object bean) {
        eventBus.unregister(bean);
    }

}
