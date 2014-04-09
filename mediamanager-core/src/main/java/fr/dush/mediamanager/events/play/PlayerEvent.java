package fr.dush.mediamanager.events.play;

import fr.dush.mediamanager.modulesapi.player.Player;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;
import java.util.List;

/**
 * Event from a player. Its subclasses can contains metadata.
 *
 * @see fr.dush.mediamanager.events.play.MoviePlayerEvent
 */
@Getter
@ToString(of = {"type", "position", "length"})
public class PlayerEvent {

    /** Video is starting to play */
    public static final int START = 1;
    /** Player quit. Media can be finished, or interrupted. */
    public static final int QUIT = 2;
    /** Stopped/paused video is restarted */
    public static final int PLAY = 3;
    /** Video is paused */
    public static final int PAUSE = 4;
    /** Video is paused */
    public static final int POSITION = 5;

    private final Player player;
    private final int type;
    private final long position;
    private final long length;
    private final List<Path> medias;

    public PlayerEvent(Player player, int type, long position, long length, List<Path> medias) {
        this.player = player;
        this.type = type;
        this.position = position;
        this.length = length;
        this.medias = medias;
    }
}
