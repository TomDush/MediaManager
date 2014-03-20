package fr.dush.mediamanager.plugins.jmplayer;

/**
 * @author Thomas Duchatelle
 */
public interface OutputListener {

    void readMPlayerLog(Level level, String line);
}
