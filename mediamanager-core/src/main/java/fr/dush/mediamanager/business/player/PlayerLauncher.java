package fr.dush.mediamanager.business.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.domain.media.MediaFile;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.VideoFile;
import fr.dush.mediamanager.events.play.MoviePlayRequestEvent;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.events.play.PlayerControlEventByType;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.exceptions.PlayerException;
import fr.dush.mediamanager.modulesapi.player.*;

/**
 * Listen to MoviePlayRequestEvent to stop others players and start the new one.
 */
@ApplicationScoped
@Startup
public class PlayerLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLauncher.class);

    @Inject
    private IMovieDAO movieDAO;

    @Inject
    private Instance<MoviePlayerWrapper> moviePlayerFactory;

    @Inject
    private Event<PlayerControlEvent> controlBus;

    /** Providers by extensions */
    private Map<String, PlayerProvider> providers = new HashMap<>();
    private PlayerProvider defaultProvider;

    @Inject
    @PostConstruct
    public void loadProviders(IModulesManager modulesManager) {
        Collection<PlayerProvider> playerProviders = modulesManager.findModuleByType(PlayerProvider.class);

        // Load players and organize them by extension. Custom configuration management is to do...
        for (PlayerProvider p : playerProviders) {
            if (defaultProvider == null) {
                defaultProvider = p;
            }

            for (String ext : p.managedExtensions()) {
                providers.put(ext.toLowerCase(), p);
            }
        }

        LOGGER.debug("Loaded players are: {} ; Default={}", playerProviders, defaultProvider);
    }

    /**
     * Play the media requested in the event after closing all other players.
     */
    public void playMovie(@Observes MoviePlayRequestEvent event) throws PlayerException {
        LOGGER.info("Play request: {}", event);

        // Close other players
        closeOtherPlayers(PlayerType.BOTH);

        // Find movie
        Movie movie = movieDAO.findById(new ObjectId(event.getMovieId()));
        if (movie == null) {
            LOGGER.warn("Movie with ID {} hasn't be found.", event.getMovieId());
            throw new PlayerException("Couldn't find movie with id '%s'", event.getMovieId());
        }

        VideoFile videoFile = MoviePlayerWrapper.findVideoFile(movie, event.getFileId());
        if (videoFile == null) {
            LOGGER.warn("Movie file with ID {} hasn't be found in movie {}.", event.getFileId(), movie);
            throw new PlayerException("Movie file with ID %s hasn't be found in movie %s.", event.getFileId(), movie);
        }

        // Get player instance
        MetaPlayer<Movie, VideoFile> player;

        PlayerProvider provider = getPlayerProvider(videoFile);
        Player implementation = provider.createPlayerInstance();

        if (implementation instanceof MetaPlayer) {
            player = (MetaPlayer<Movie, VideoFile>) implementation;
            player.initialise(generateId(), movie, videoFile, null);
        }
        else if (implementation instanceof EmbeddedPlayer) {
            player = moviePlayerFactory.get();
            player.initialise(generateId(), movie, videoFile, (EmbeddedPlayer) implementation);
        }
        else {
            throw new ConfigurationException("Player must implement MetaPlayer or EmbeddedPlayer to read a Movie");
        }

        player.play();
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    private void closeOtherPlayers(PlayerType type) {
        controlBus.fire(new PlayerControlEventByType(PlayerControlEvent.PlayerControl.STOP, type));
    }

    private PlayerProvider getPlayerProvider(MediaFile videoFile) {
        PlayerProvider provider = providers.get(Files.getFileExtension(videoFile.getFile()).toLowerCase());

        return provider == null ? defaultProvider : provider;
    }

}
