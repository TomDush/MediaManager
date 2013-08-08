package fr.dush.mediamanager.exceptions;

/**
 * Exception throws during initialisation...
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
public class InitializationException extends RuntimeException {

	public InitializationException() {
		super();
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}

}
