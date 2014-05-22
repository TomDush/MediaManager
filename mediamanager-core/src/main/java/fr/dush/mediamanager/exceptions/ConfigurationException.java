package fr.dush.mediamanager.exceptions;

@SuppressWarnings("serial")
public class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String pattern, Object... args) {
        super(String.format(pattern, args));
    }

    public ConfigurationException(Throwable cause, String pattern, Object... args) {
        super(String.format(pattern, args), cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
