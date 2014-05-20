package fr.dush.mediamanager.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ask to register this component to Guava Event Bus */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventBusRegisterEvent extends AbstractEvent {

    /** TRUE to register, FALSE to unregister */
    private final boolean register;

    public EventBusRegisterEvent(Object source, boolean register) {
        super(source);
        this.register = register;
    }
}
