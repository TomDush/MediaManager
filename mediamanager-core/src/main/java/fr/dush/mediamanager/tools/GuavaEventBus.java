package fr.dush.mediamanager.tools;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure Guava Event Bus to get CDI event and broadcast them to it.
 */
@Configuration
public class GuavaEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaEventBus.class);

    private static final EventBus eventBus = new EventBus();

    @Bean
    public EventBus createGuavaEventBus() {
        return eventBus;
    }

    public static void register(Object bean) {
        LOGGER.debug("Register bean: {}", bean);
        eventBus.register(bean);
    }

    public static void unregister(Object bean) {
        eventBus.unregister(bean);
    }

}
