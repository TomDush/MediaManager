package fr.dush.mediamanager.exceptions;

@SuppressWarnings("serial")
public class ScanException extends Exception {

	public ScanException() {
		super();
	}

	public ScanException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScanException(String message) {
		super(message);
	}

	public ScanException(Throwable cause) {
		super(cause);
	}

}
