package fr.dush.mediamanager.business.player;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.domain.media.MediaSummary;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.media.Recovery;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.events.mediatech.MovieAdminEvent;
import fr.dush.mediamanager.events.mediatech.Operation;
import fr.dush.mediamanager.events.play.MoviePlayerEvent;
import fr.dush.mediamanager.events.play.PlayerEvent;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * This service listening players events to mark movies as seen, and be able to restart movie from where it has been
 * stopped.
 */
@Named
public class RecoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryService.class);

    /** Minimum ratio to assume movie is finished */
    public static final double FINISHED_RATIO = 0.93;
    /** Minimum ratio to assume movie is started */
    public static final double STARTED_RATIO = 0.05;

    @Inject
    private IMovieDAO movieDAO;

    @Inject
    private IRecoveryDAO recoveryDAO;

    @Inject
    private Mapper mapper;

    @Subscribe
    public void handleMovieEvents(MoviePlayerEvent event) {
        if (event.getType() == PlayerEvent.QUIT) {
            LOGGER.debug("QUIT event: {}", event);

            double ratio = event.getLength() > 0 ? (double) event.getPosition() / (double) event.getLength() : 0;
            if (ratio > FINISHED_RATIO) {
                LOGGER.info("Mark movie as read: {}", event.getMovie().getTitle());
                markMovieHasSeen(event.getMovie());

            } else if (ratio > STARTED_RATIO) {
                LOGGER.info("Save recovery for {}", event.getMovie().getTitle());
                saveRecovery(event.getPosition(), event.getLength(), event.getMovie(), event.getMedias());
            }
        }
    }

    /** Listen all movies events and execute expected REMOVE_RESUME operation */
    @Subscribe
    public void handleOperation(MovieAdminEvent event) {
        if (event.getOperation() == Operation.REMOVE_RESUME) {
            recoveryDAO.delete(new MediaReference(MediaType.MOVIE, event.getId()));
            event.markHandled();
        }
    }

    private void saveRecovery(long position, long length, Movie movie, List<Path> medias) {
        MediaSummary summary = mapper.map(movie, MediaSummary.class);
        summary.setMediaType(MediaType.MOVIE);

        Recovery recovery = new Recovery(summary);
        recovery.setPosition(position);
        recovery.setLength(length);
        recovery.setMediaFiles(transform(medias, new Function<Path, String>() {

            @Override
            public String apply(Path input) {
                return input.toAbsolutePath().toString();
            }
        }));

        recovery.setMediaSummary(summary);

        // This save will update existing records if any
        recoveryDAO.save(recovery);
    }

    private void markMovieHasSeen(Movie movie) {
        movieDAO.incrementViewCount(movie.getId(), 1);
        recoveryDAO.delete(new MediaReference(MediaType.MOVIE, movie.getId()));
    }
}
