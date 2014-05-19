package fr.dush.mediamanager.business.mediatech;

import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.events.mediatech.AdminEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle every admin event and log those which wasn't handled.
 *
 * @author Thomas Duchatelle
 */
public class DeadLetterAdminEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterAdminEvent.class);

    @Subscribe
    public void logNotHandledEvent(AdminEvent event) {
        // TODO Must handle only not handled event!
        if (!event.isHandled()) {
            LOGGER.warn("Operation [{}] unsupported, event wasn't handled: {}", event.getOperation(), event);
        }
    }
}
