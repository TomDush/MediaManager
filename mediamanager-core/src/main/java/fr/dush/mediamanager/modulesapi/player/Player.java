package fr.dush.mediamanager.modulesapi.player;

/**
 * @author Thomas Duchatelle
 */
public interface Player {

    /** Quit and return position, quit command could be asynchronous */
    long quit();

    /** Toggle pause/play, return current position */
    long pause();

    /** Return if mplayer instance is still alive, return true even if it's paused. */
    boolean isActive();

    long getPosition();

    void setPosition(long position);

    long getTotalLength();

    boolean isPaused();

    /** Return resources used by player */
    PlayerType getType();

    /**
     * Move time after (or before if time is negative).
     * 
     * @param time Value in second (beware, depending on implementation, it could be something else!)
     */
    void seek(int time);
}
