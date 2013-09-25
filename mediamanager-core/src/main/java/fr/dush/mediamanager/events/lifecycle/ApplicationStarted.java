package fr.dush.mediamanager.events.lifecycle;

import fr.dush.mediamanager.events.AbstractEvent;

@SuppressWarnings("serial")
public class ApplicationStarted extends AbstractEvent {

	public ApplicationStarted(Object source) {
		super(source);
	}

}
