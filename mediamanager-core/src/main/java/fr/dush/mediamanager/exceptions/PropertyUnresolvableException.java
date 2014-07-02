package fr.dush.mediamanager.exceptions;

/**
 * Exception raised if a property is not defined.
 */
public class PropertyUnresolvableException extends ConfigurationException {

    public PropertyUnresolvableException() {
    }

    public PropertyUnresolvableException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyUnresolvableException(String message) {
        super(message);
    }

    public PropertyUnresolvableException(String pattern, Object... args) {
        super(pattern, args);
    }

    public PropertyUnresolvableException(Throwable cause, String pattern, Object... args) {
        super(cause, pattern, args);
    }

    public PropertyUnresolvableException(Throwable cause) {
        super(cause);
    }
}
