package fr.dush.mediamanager.exceptions;

@SuppressWarnings("serial")
public class PlayerException extends Exception {

    public PlayerException() {
        super();
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerException(String pattern, Object... args) {
        super(String.format(pattern, args));
    }

    public PlayerException(Throwable cause) {
        super(cause);
    }

}
