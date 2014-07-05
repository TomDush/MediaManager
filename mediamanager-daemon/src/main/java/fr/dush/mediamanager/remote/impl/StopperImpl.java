package fr.dush.mediamanager.remote.impl;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.events.lifecycle.ApplicationStarted;
import fr.dush.mediamanager.remote.IStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Class notified when received event to close application. <p> Use internal singleton to resolve concurrency locks from
 * CDI's proxy. </p>
 *
 * @author Thomas Duchatelle
 */
@Service
public class StopperImpl implements IStopper, ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopperImpl.class);

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private EventBus eventBus;

    @Override
    public void stopApplication() {
        LOGGER.debug("Stopping application...");

        if (applicationContext instanceof Lifecycle) {
            ((Lifecycle) applicationContext).stop();
        } else {
            LOGGER.error("Couldn't stop Spring context: {} is not instance of Lifecycle.", applicationContext);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextStartedEvent) {
            LOGGER.debug("Fire application is started on Guava EventBus. Event={}", event);
            eventBus.post(new ApplicationStarted(this));
        }
    }
}
