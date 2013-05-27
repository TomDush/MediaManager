package fr.dush.mediamanager.exceptions;

@SuppressWarnings("serial")
public class ModuleLoadingException extends Exception {

	public ModuleLoadingException() {
		super();
	}

	public ModuleLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleLoadingException(String message) {
		super(message);
	}

	public ModuleLoadingException(Throwable cause) {
		super(cause);
	}

}
