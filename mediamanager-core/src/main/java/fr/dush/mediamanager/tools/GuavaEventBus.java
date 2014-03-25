package fr.dush.mediamanager.tools;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.events.EventBusRegisterEvent;

/**
 * Configure Guava Event Bus to get CDI event and broadcast them to it.
 */
@ApplicationScoped
public class GuavaEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaEventBus.class);

    private static final EventBus eventBus = new EventBus();

    @Produces
    @ApplicationScoped
    private EventBus createGuavaEventBus() {
        return eventBus;
    }

    public void broadcastEveryEvent(@Observes Object event) {
        if (event instanceof EventBusRegisterEvent) {
            if (((EventBusRegisterEvent) event).isRegister()) {
                eventBus.register(((EventBusRegisterEvent) event).getSource());
            }
            else {
                eventBus.unregister(this);
            }

        }
        else {
            LOGGER.debug("Broadcast event: {}", event);
            eventBus.post(event);

        }
    }

    public static void register(Object bean) {
        LOGGER.debug("Register bean: {}", bean);
        eventBus.register(bean);
    }

    public static void unregister(Object bean) {
        eventBus.unregister(bean);
    }

}
