package fr.dush.mediamanager.events.mediatech;

import fr.dush.mediamanager.events.AbstractEvent;
import lombok.Data;

/**
 * Event to administrate mediatech.
 *
 * @author Thomas Duchatelle
 */
@Data
public abstract class AdminEvent extends AbstractEvent {

    private Operation operation;

    private boolean handled = false;

    protected AdminEvent(Object source, Operation operation) {
        super(source);
        this.operation = operation;
    }

    public void markHandled() {
        handled = true;
    }
}
