package fr.dush.mediamanager.modulesapi.enrich;

/**
 * Exception thrown on data enrichment...
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
public class EnrichException extends Exception {

	public EnrichException() {
		super();
	}

	public EnrichException(String message, Throwable cause) {
		super(message, cause);
	}

	public EnrichException(String message) {
		super(message);
	}

	public EnrichException(Throwable cause) {
		super(cause);
	}

}
