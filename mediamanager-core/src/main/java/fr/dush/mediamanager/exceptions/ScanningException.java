package fr.dush.mediamanager.exceptions;

@SuppressWarnings("serial")
public class ScanningException extends Exception {

	public ScanningException() {
		super();
	}

	public ScanningException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScanningException(String message) {
		super(message);
	}

	public ScanningException(Throwable cause) {
		super(cause);
	}

}
