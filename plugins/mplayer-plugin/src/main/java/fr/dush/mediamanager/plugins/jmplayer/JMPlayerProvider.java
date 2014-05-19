package fr.dush.mediamanager.plugins.jmplayer;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.modulesapi.player.Player;
import fr.dush.mediamanager.modulesapi.player.PlayerProvider;

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
    private EventBus eventBus;

    @Override
    public Player createPlayerInstance() {
        return new JMPlayer(eventBus, MPLAYER_PATH, MPLAYER_OPTIONS);
    }

    @Override
    public List<String> managedExtensions() {
        return newArrayList("avi", "mp4", "mkv", "m4v", "divx");
    }
}
