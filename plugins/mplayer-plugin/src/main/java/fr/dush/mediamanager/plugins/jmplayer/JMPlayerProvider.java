package fr.dush.mediamanager.plugins.jmplayer;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.modulesapi.player.Player;
import fr.dush.mediamanager.events.play.PlayerEvent;
import fr.dush.mediamanager.modulesapi.player.PlayerProvider;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * Provide JMPlayer instance to play video with MPlayer
 *
 * @author Thomas Duchatelle
 */
@Module(id = "jmplayer", name = "MPlayer-plugin")
public class JMPlayerProvider implements PlayerProvider {

    // TODO Following constants should be configuration
    public static final String MPLAYER_PATH = "/usr/bin/mplayer";
    public static final String MPLAYER_OPTIONS = "-fs";

    @Inject
    private Event<PlayerEvent> playerEventBus;

    @Override
    public Player createPlayerInstance() {
        return new JMPlayer(playerEventBus, MPLAYER_PATH, MPLAYER_OPTIONS);
    }

    @Override
    public List<String> managedExtensions() {
        return newArrayList("avi", "mp4", "mkv", "m4v", "divx");
    }
}
