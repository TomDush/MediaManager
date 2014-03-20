package fr.dush.mediamanager.plugins.jmplayer;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Thomas Duchatelle
 */
@Getter
@ToString(of = {"type", "position", "length"})
public class PlayerEvent {

    /** Video is starting to play */
    public static final int START = 1;
    /** Video is finished */
    public static final int FINISHED = 2;
    /** Stopped/paused video is restarted */
    public static final int PLAY = 3;
    /** Video is paused */
    public static final int PAUSE = 4;
    /** Video is paused */
    public static final int POSITION = 5;

    private final JMPlayer player;
    private final int type;
    private final long position;
    private final long length;
    private final List<Path> medias;

    public PlayerEvent(JMPlayer player, int type, long position, long length, List<Path> medias) {
        this.player = player;
        this.type = type;
        this.position = position;
        this.length = length;
        this.medias = medias;
    }
}
