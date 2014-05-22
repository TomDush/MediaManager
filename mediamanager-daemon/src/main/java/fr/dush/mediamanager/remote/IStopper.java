package fr.dush.mediamanager.remote;

/**
 * @author Thomas Duchatelle
 */
public interface IStopper {

    void stopApplication();

    void waitApplicationEnd() throws InterruptedException;

    void fireApplicationStarted(Object source);
}
