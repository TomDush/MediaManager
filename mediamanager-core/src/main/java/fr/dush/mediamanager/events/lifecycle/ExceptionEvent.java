package fr.dush.mediamanager.events.lifecycle;

import lombok.Getter;
import lombok.Setter;
import fr.dush.mediamanager.events.AbstractEvent;

@Getter
@Setter
@SuppressWarnings("serial")
public class ExceptionEvent extends AbstractEvent {

	private String message;

	private Throwable exception;

	public ExceptionEvent(Object source, String message, Throwable exception) {
		super(source);
		this.message = message;
		this.exception = exception;
	}

	public ExceptionEvent(Object source, Throwable exception) {
		this(source, exception.getMessage(), exception);
	}

	public ExceptionEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

}
