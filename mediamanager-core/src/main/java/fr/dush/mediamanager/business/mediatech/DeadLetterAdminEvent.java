package fr.dush.mediamanager.business.mediatech;

import fr.dush.mediamanager.events.mediatech.AdminEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

/**
 * Handle every admin event and log those which wasn't handled.
 *
 * @author Thomas Duchatelle
 */
public class DeadLetterAdminEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterAdminEvent.class);

    public void logNotHandledEvent(@Observes(during = TransactionPhase.AFTER_COMPLETION) AdminEvent event) {
        if (!event.isHandled()) {
            LOGGER.warn("Operation [{}] unsupported, event wasn't handled: {}", event.getOperation(), event);
        }
    }
}
