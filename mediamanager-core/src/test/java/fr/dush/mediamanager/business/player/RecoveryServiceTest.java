package fr.dush.mediamanager.business.player;

import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.engine.ArgThat;
import fr.dush.mediamanager.events.play.MoviePlayerEvent;
import fr.dush.mediamanager.events.play.PlayerEvent;
import fr.dush.mediamanager.tools.DozerMapperFactory;
import org.bson.types.ObjectId;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Paths;

import static com.google.common.collect.Lists.*;
import static org.mockito.Mockito.*;

/**
 * @author Thomas Duchatelle
 */
@RunWith(MockitoJUnitRunner.class)
public class RecoveryServiceTest {

    @InjectMocks
    private RecoveryService recoveryService;

    @Mock
    private IRecoveryDAO recoveryDAO;

    @Mock
    private IMovieDAO movieDAO;

    @Spy
    private Mapper mapper = new DozerMapperFactory().getDozerMapper();

    @Test
    public void testInterruptMovie() throws Exception {
        MoviePlayerEvent movieEvent = newMovieEvent(76);

        // Exec
        recoveryService.handleMovieEvents(movieEvent);

        // Assert
        verify(recoveryDAO).save(ArgThat.argRecovery(76));
        verifyNoMoreInteractions(recoveryDAO);
    }

    private MoviePlayerEvent newMovieEvent(int position) {
        PlayerEvent event = new PlayerEvent(null,
                                            PlayerEvent.QUIT,
                                            position,
                                            100,
                                            newArrayList(Paths.get("hello_CD1.avi"), Paths.get("hello_CD2.avi")));

        Movie movie = new Movie();
        movie.setId(new ObjectId("5240760958eff5a9e1d18203"));
        return new MoviePlayerEvent(event, movie);
    }

    @Test
    public void testStopMovie() throws Exception {
        // Exec
        recoveryService.handleMovieEvents(newMovieEvent(95));

        // Assert
        verify(recoveryDAO).delete(ArgThat.argReference(MediaType.MOVIE, "5240760958eff5a9e1d18203"));
        verify(movieDAO).incrementViewCount(new ObjectId("5240760958eff5a9e1d18203"), 1);
        verifyNoMoreInteractions(recoveryDAO);
    }

    @Test
    public void testNotReallyStartedMovie() throws Exception {

        // Exec
        recoveryService.handleMovieEvents(newMovieEvent(4));

        // Assert
        verifyZeroInteractions(recoveryDAO);
    }

}
