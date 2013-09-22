package fr.dush.mediamanager.events.lifecycle;

import fr.dush.mediamanager.events.AbstractEvent;

@SuppressWarnings("serial")
public class ApplicationStopEvent extends AbstractEvent {

	public ApplicationStopEvent(Object source) {
		super(source);
	}

}
