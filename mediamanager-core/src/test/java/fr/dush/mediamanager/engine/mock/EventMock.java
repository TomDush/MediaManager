package fr.dush.mediamanager.engine.mock;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.annotations.Module;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module annotation is here to avoid ambiguous Event's declarations.
 *
 * @author Thomas Duchatelle
 */
@Module(id = "not-managed", name = "EventMock")
@Getter
public class EventMock<T> extends EventBus {
    // TODO Use this mock in Spring context

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMock.class);

    @Override
    public void post(Object event) {
        LOGGER.info("Fire event: {}", event);
    }
}
