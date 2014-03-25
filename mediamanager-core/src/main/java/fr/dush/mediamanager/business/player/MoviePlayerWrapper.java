package fr.dush.mediamanager.business.player;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.VideoFile;
import fr.dush.mediamanager.events.EventBusRegisterEvent;
import fr.dush.mediamanager.events.play.MoviePlayerEvent;
import fr.dush.mediamanager.events.play.PlayerCollectorEvent;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.events.play.PlayerEvent;
import fr.dush.mediamanager.exceptions.PlayerException;
import fr.dush.mediamanager.modulesapi.player.EmbeddedPlayer;
import fr.dush.mediamanager.modulesapi.player.MetaPlayer;
import fr.dush.mediamanager.modulesapi.player.PlayerType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Collections2.*;

/**
 * This wrapper append meta data management to player and integration to CDI.
 */
public class MoviePlayerWrapper implements MetaPlayer<Movie, VideoFile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviePlayerWrapper.class);
    public static final int MIN = 60;
    public static final int SEEK_DELAY = 3 * MIN;

    @Inject
    @Setter
    private Event<PlayerEvent> busEvent;

    @Inject
    private Event<EventBusRegisterEvent> guavaBusEvent;

    @Getter
    private String id;

    @Getter
    private Movie media;
    @Getter
    private VideoFile file;

    /** Real player implementation */
    private EmbeddedPlayer wrappedPlayer;

    @PostConstruct
    public void logCreation() {
        LOGGER.debug("Just create a MoviePlayerWrapper: {}", this);
        guavaBusEvent.fire(new EventBusRegisterEvent(this, true));
    }

    @Override
    public void initialise(String id, Movie media, VideoFile file, EmbeddedPlayer embeddedPlayer) {
        this.id = id;
        this.media = media;
        this.file = file;
        this.wrappedPlayer = embeddedPlayer;

        wrappedPlayer.setBusEvent(new Event<PlayerEvent>() {

            @Override
            public void fire(PlayerEvent playerEvent) {
                fireEvent(playerEvent);
            }

            @Override
            public Event<PlayerEvent> select(Annotation... annotations) {
                throw new IllegalStateException("Method not implemented");
            }

            @Override
            public <U extends PlayerEvent> Event<U> select(Class<U> uClass, Annotation... annotations) {
                throw new IllegalStateException("Method not implemented");
            }

            @Override
            public <U extends PlayerEvent> Event<U> select(TypeLiteral<U> uTypeLiteral, Annotation... annotations) {
                throw new IllegalStateException("Method not implemented");
            }
        });

    }

    @Override
    public void play() throws PlayerException {
        // Read video path(s) ...
        List<Path> paths = new ArrayList<>();
        paths.add(Paths.get(file.getFile()));
        paths.addAll(transform(file.getNextParts(), new Function<String, Path>() {

            @Override
            public Path apply(String input) {
                return Paths.get(input);
            }
        }));

        // Start reading
        try {
            LOGGER.info("Read files: {}", paths);
            wrappedPlayer.play(paths);

        } catch (IOException e) {
            throw new PlayerException("Couldn't read files {}: {}", paths, e.getMessage(), e);
        }
    }

    @Subscribe
    public void receivedControl(PlayerControlEvent event) {
        LOGGER.debug("-->{}.receivedControl({})", this, event);

        if (event.isConcerned(this)) {

            switch (event.getRequest()) {
                case STOP:
                    wrappedPlayer.quit();
                    break;

                case PLAY:
                case PAUSE:
                case TOGGLE_PAUSE:
                    wrappedPlayer.pause();
                    break;

                case JUMP_FORWARD:
                    wrappedPlayer.seek(SEEK_DELAY);
                    break;

                case JUMP_BACK:
                    wrappedPlayer.seek(-SEEK_DELAY);
                    break;

                case JUMP_TO:
                    wrappedPlayer.setPosition(event.getValue());

                default:
                    LOGGER.warn("Command {} is not available for Movie player.");
            }

        }

    }

    @Subscribe
    public void registerPlayer(PlayerCollectorEvent event) {
        LOGGER.debug("-->{}.registerPlayer({})", this, event);
        if (isActive()) {
            event.registerPlayer(this);
        } else {
            guavaBusEvent.fire(new EventBusRegisterEvent(this, false));
        }
    }

    /** Intercept event launched by the wrapped player and enrich it. */
    protected void fireEvent(PlayerEvent playerEvent) {
        busEvent.fire(new MoviePlayerEvent(playerEvent, media));
    }

    @Override
    public long quit() {
        return wrappedPlayer.quit();
    }

    @Override
    public long pause() {
        return wrappedPlayer.pause();
    }

    @Override
    public PlayerType getType() {
        return wrappedPlayer.getType();
    }

    @Override
    public void seek(int time) {
        wrappedPlayer.seek(time);
    }

    @Override
    public boolean isActive() {
        return wrappedPlayer.isActive();
    }

    @Override
    public long getPosition() {
        return wrappedPlayer.getPosition();
    }

    @Override
    public void setPosition(long position) {
        wrappedPlayer.setPosition(position);
    }

    @Override
    public long getTotalLength() {
        return wrappedPlayer.getTotalLength();
    }

    @Override
    public boolean isPaused() {
        return wrappedPlayer.isPaused();
    }

    public static VideoFile findVideoFile(Movie movie, String fileId) {
        // Find video
        for (VideoFile videoFile : movie.getVideoFiles()) {
            if (StringUtils.equals(videoFile.getFile(), fileId)) {
                return videoFile;
            }
        }

        return null;
    }
}
