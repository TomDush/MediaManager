package fr.dush.mediamanager.plugins.amarok;

import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.business.player.AbstractMetaPlayer;
import fr.dush.mediamanager.domain.media.Media;
import fr.dush.mediamanager.domain.media.MediaFile;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.exceptions.PlayerException;
import fr.dush.mediamanager.modulesapi.player.EmbeddedPlayer;
import fr.dush.mediamanager.modulesapi.player.PlayerType;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.commons.lang3.StringUtils.*;

@ApplicationScoped
@Startup
public class AmarokPlayer extends AbstractMetaPlayer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmarokPlayer.class);

    private static final String BINARY = "/usr/bin/amarok";

    @Getter
    private boolean paused = false;

    @Override
    public String getName() {
        return "Amarok";
    }

    @Override
    public Media getMedia() {
        return null;
    }

    @Override
    public MediaFile getFile() {
        return null;
    }

    @Override
    public void initialise(Media media, MediaFile file, EmbeddedPlayer embeddedPlayer) {

    }

    @Override
    public void play() throws PlayerException {
        execute("--play");
        paused = false;
    }

    /** Never quit Amarok, just stop music */
    @Override
    public long quit() {
        return 0;
    }

    @Override
    public boolean isActive() {
        try {
            Process process = Runtime.getRuntime().exec("ps -e");
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = outputReader.readLine()) != null) {
                if (isNotEmpty(line) && line.contains("amarok")) {
                    return true;
                }
            }

            return false;

        } catch (IOException e) {
            LOGGER.error("Could not determine if Amarok is running.", e);
            return false;
        }
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public void setPosition(long position) {

    }

    @Override
    public long getTotalLength() {
        return 0;
    }

    /** Do nothing if flag 'paused' isn't up to date. */
    @Override
    public long pause() {
        if (isActive()) {
            execute(paused ? "--play" : "--pause");
            paused = !paused;
        }

        return 0;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.AUDIO;
    }

    @Override
    public void seek(int time) {

    }

    protected void handleControl(PlayerControlEvent.PlayerControl request, long value) throws PlayerException {
        switch (request) {
            case STOP:
                execute("--pause");
                paused = true;
                break;

            case JUMP_FORWARD:
                execute("--next");
                break;

            case JUMP_BACK:
                execute("--previous");
                break;

            default:
                // Delegate control to super class with default control mapping
                super.handleControl(request, value);
        }
    }

    /** Execute a given command with Amarok executable */
    private void execute(String command) {
        try {
            Runtime.getRuntime().exec(BINARY + " " + command);

        } catch (IOException e) {
            LOGGER.error("Can't execute amarok command: {}", command);
        }
    }
}
