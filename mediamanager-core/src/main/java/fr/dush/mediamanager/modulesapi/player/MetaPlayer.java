package fr.dush.mediamanager.modulesapi.player;

import fr.dush.mediamanager.domain.media.Media;
import fr.dush.mediamanager.domain.media.MediaFile;
import fr.dush.mediamanager.exceptions.PlayerException;

/**
 * Player aware of metadata and connected to CDI events
 * 
 * @param <M> Media type managed by this player
 * @param <F> File type managed by this player
 */
public interface MetaPlayer<M extends Media, F extends MediaFile> extends Player {

    /** Get unique identifier */
    String getId();

    /** Get reading media */
    M getMedia();

    /** Get reading file (useful for shows, to know episode) */
    F getFile();

    /**
     * Initialise player with metadata, real implementation to use, ...
     * 
     * @param id Unique media identifier
     * @param media Media will be read
     * @param file MediaFile to read
     * @param embeddedPlayer Real implementation to use
     */
    void initialise(String id, M media, F file, EmbeddedPlayer embeddedPlayer);

    /** Just start to play from beginning */
    void play() throws PlayerException;

    /** Quit and return position, quit command could be asynchronous */
    long quit();

    /** Return if MPlayer instance is still alive, return true even if it's paused. */
    boolean isActive();

    /** Return resources used by player */
    PlayerType getType();
}
