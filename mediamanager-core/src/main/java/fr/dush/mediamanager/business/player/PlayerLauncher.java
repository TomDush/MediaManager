package fr.dush.mediamanager.business.player;

import com.google.common.io.Files;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.MediaFile;
import fr.dush.mediamanager.domain.media.Recovery;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.VideoFile;
import fr.dush.mediamanager.events.play.PlayRequestEvent;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.events.play.PlayerControlEventByType;
import fr.dush.mediamanager.events.play.ResumeRequestEvent;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.exceptions.PlayerException;
import fr.dush.mediamanager.modulesapi.player.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Listen to PlayRequestEvent to stop others players and start the new one.
 */
@ApplicationScoped
@Startup
public class PlayerLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLauncher.class);

    @Inject
    private IMovieDAO movieDAO;
    @Inject
    private IRecoveryDAO recoveryDAO;

    @Inject
    private Instance<MoviePlayerWrapper> moviePlayerFactory;

    @Inject
    private Event<PlayerControlEvent> controlBus;

    // Used only by post construct...
    @Inject
    private IModulesManager modulesManager;

    /** Providers by extensions */
    private Map<String, PlayerProvider> providers = new HashMap<>();
    private PlayerProvider defaultProvider;

    @PostConstruct
    public void loadProviders() {
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
    public void playMedia(@Observes PlayRequestEvent event) throws PlayerException {
        LOGGER.info("Play request: {}", event);

        // Start playing...
        playMovie(event.getMovieId(), event.getFileId(), 0);

    }

    private void playMovie(String movieId, String fileId, long position) throws PlayerException {
        // Close other players
        closeOtherPlayers(PlayerType.BOTH);

        // Find movie
        Movie movie = movieDAO.findById(new ObjectId(movieId));
        if (movie == null) {
            LOGGER.warn("Movie with ID {} hasn't be found.", movieId);
            throw new PlayerException("Couldn't find movie with id '%s'", movieId);
        }

        VideoFile videoFile = MoviePlayerWrapper.findVideoFile(movie, fileId);
        if (videoFile == null) {
            LOGGER.warn("Movie file with ID {} hasn't be found in movie {}.", fileId, movie);
            throw new PlayerException("Movie file with ID %s hasn't be found in movie %s.", fileId, movie);
        }

        // Get player instance
        MetaPlayer<Movie, VideoFile> player;

        PlayerProvider provider = getPlayerProvider(videoFile);
        Player implementation = provider.createPlayerInstance();

        if (implementation instanceof MetaPlayer) {
            player = (MetaPlayer<Movie, VideoFile>) implementation;
            player.initialise(movie, videoFile, null);
        } else if (implementation instanceof EmbeddedPlayer) {
            player = moviePlayerFactory.get();
            player.initialise(movie, videoFile, (EmbeddedPlayer) implementation);
        } else {
            throw new ConfigurationException("Player must implement MetaPlayer or EmbeddedPlayer to read a Movie");
        }

        player.play();

        // If resume process, set new position
        if (position > 0) {
            player.setPosition(position);
        }
    }

    public void resume(@Observes ResumeRequestEvent event) throws PlayerException {
        Recovery recovery = recoveryDAO.findById(event.getReference());
        if (recovery == null) {
            LOGGER.warn("No media to recover with ID: {}", recovery);

        } else {
            playMovie(recovery.getMediaSummary().getId(), recovery.getMediaFiles().get(0), recovery.getPosition());
        }
    }

    private void closeOtherPlayers(PlayerType type) {
        controlBus.fire(new PlayerControlEventByType(PlayerControlEvent.PlayerControl.STOP, type));
    }

    private PlayerProvider getPlayerProvider(MediaFile videoFile) {
        PlayerProvider provider = providers.get(Files.getFileExtension(videoFile.getFile()).toLowerCase());

        return provider == null ? defaultProvider : provider;
    }

}
