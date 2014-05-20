package fr.dush.mediamanager.modulesapi.player;

import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Some more methods which must be implemented by all players */
public interface EmbeddedPlayer extends Player {

    /** Start playing 1 file */
    void play(Path path) throws IOException;

    void setEventBus(EventBus onPlayerEvent);

    /** Start file to read together as 1 bigger file */
    void play(List<Path> paths) throws IOException;
}
