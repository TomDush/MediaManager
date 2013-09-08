package fr.dush.mediamanager.events;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Base for each MediaManager's events.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class AbstractEvent implements Serializable {

	/** Object which generate this event */
	private Object source;

	public AbstractEvent(Object source) {
		this.source = source;
	}

}
