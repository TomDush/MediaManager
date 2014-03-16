package fr.dush.mediamanager.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Duchatelle
 */
public class RetryApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(RetryApi.class);

    public static final int MAX_TRY = 5;
    public static final int WAIT_TIME = 5000;

    /**
     * Retry MAX_TRY before giving up.
     *
     * @throws RuntimeException This exception encapsulate the real exception.
     */
    public static <T> T retry(MethodWithReturn<T> methodWithReturn) throws RuntimeException {
        int n = 0;
        Exception exception = null;

        try {

            while (n++ <= MAX_TRY) {
                try {
                    // Try to do it and accept 5 fails
                    return methodWithReturn.doIt();
                } catch (Exception e) {
                    LOGGER.warn("Error occurred: {}... Will retrying", e.getMessage());
                    exception = e;

                    Thread.sleep(WAIT_TIME);
                }
            }
        } catch (InterruptedException e1) {
            LOGGER.debug("Do not retry any more...", e1.getMessage());
        }

        throw new RuntimeException("Could not get information even after retrying " + MAX_TRY + " times.", exception);
    }

    public interface MethodWithReturn<T> {

        T doIt() throws Exception;
    }
}
