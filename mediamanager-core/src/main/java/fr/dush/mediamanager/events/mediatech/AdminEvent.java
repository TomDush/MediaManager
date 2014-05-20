package fr.dush.mediamanager.events.mediatech;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.events.AbstractEvent;

/**
 * Event to administrate mediatech.
 * 
 * @author Thomas Duchatelle
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
