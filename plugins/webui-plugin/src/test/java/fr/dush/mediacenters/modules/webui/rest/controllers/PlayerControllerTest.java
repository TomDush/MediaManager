package fr.dush.mediacenters.modules.webui.rest.controllers;

import fr.dush.mediacenters.modules.webui.rest.dto.PlayerInfo;
import fr.dush.mediamanager.business.player.MoviePlayerWrapper;
import fr.dush.mediamanager.domain.media.Assertions;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.video.*;
import fr.dush.mediamanager.events.play.PlayRequestEvent;
import fr.dush.mediamanager.events.play.PlayerCollectorEvent;
import fr.dush.mediamanager.modulesapi.player.EmbeddedPlayer;
import fr.dush.mediamanager.tools.DozerMapperFactory;
import org.bson.types.ObjectId;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.enterprise.event.Event;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.collect.Sets.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerControllerTest {

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final long POSITION = 12L;
    public static final long LENGTH = 42L;

    @InjectMocks
    private PlayerController playerController;

    @Spy
    private Mapper dozerMapper = new DozerMapperFactory().getDozerMapper();

    @Mock
    private Event<PlayRequestEvent> bus;
    @Mock
    private Event<PlayerCollectorEvent> collectorBus;

    @Spy
    private MoviePlayerWrapper player = new MoviePlayerWrapper();

    private Movie movie;
    private VideoFile videoFile;

    @Mock
    private EmbeddedPlayer internalPlayer;

    @Before
    public void setUp() throws Exception {
        movie = newMovie();
        videoFile = new VideoFile();

        player.initialise(movie, videoFile, internalPlayer);

        when(internalPlayer.getPosition()).thenReturn(POSITION);
        when(internalPlayer.getTotalLength()).thenReturn(LENGTH);
        when(internalPlayer.isPaused()).thenReturn(true);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PlayerCollectorEvent event = (PlayerCollectorEvent) invocation.getArguments()[0];
                event.registerPlayer(player);

                return null;
            }
        }).when(collectorBus).fire(any(PlayerCollectorEvent.class));
    }

    @Test
    public void test_getPlaysInProgress() throws Exception {
        List<PlayerInfo> infoList = playerController.getPlaysInProgress();
        assertThat(infoList).hasSize(1);

        Assertions.assertThat(infoList.get(0)).hasLength(LENGTH).hasPosition(POSITION);
        Assertions.assertThat(infoList.get(0).getMedia())
                  .hasId("5200c7a884ae0d25732cd70a")
                  .hasPoster("/some/poster.jpg")
                  .hasTitle("Iron Man 1")
                  .hasGenres("action");
    }

    public Movie newMovie() throws ParseException {
        Movie m = new Movie();
        m.setId(new ObjectId("5200c7a884ae0d25732cd70a"));

        m.setTitle("Iron Man 1");
        m.getMediaIds().addId("imdb", "0123654789");
        m.getMediaIds().addId("junit", "IRONMAN_1");

        m.setGenres(newHashSet("action"));
        m.setSeen(2);
        m.setCreation(FORMATTER.parse("2013-08-05"));
        m.setOtherMetaData("No other data on this films...");
        m.setOverview("Heroes aren't born. They're built.");
        m.setPoster("/some/poster.jpg");
        m.setRelease(FORMATTER.parse("2008-05-01"));

        final Trailers ts = new Trailers();
        ts.setRefreshed(FORMATTER.parse("2013-08-05"));
        ts.getSources().add("MOVIES_DB");
        final Trailer t = new Trailer();
        t.setPublishDate(FORMATTER.parse("2008-05-01"));
        t.setQuality("HD1080");
        t.setSource("youtube");
        t.setTitle("Iron Man (trailer)");
        t.setTrailer(new VideoFile(Paths.get("/media/trailers/paths/ironman_1_HD_trailer.mp4")));
        t.setUrl("http://www.youtube.com/watch?v=KAE5ymVLmZg");
        ts.getTrailers().add(t);
        m.setTrailers(ts);

        m.getBackdrops().add("/media/backdrops/ironman_1.jpg");
        m.getDirectors().add(new Person("Jon Favreau", new SourceId("MovieDB", "15277")));
        m.getMainActors().add(new Person("Robert Downey Jr.", new SourceId("MovieDB", "3223")));
        m.getVideoFiles().add(new VideoFile(Paths.get("media/movies/ironman_1.mp4")));

        return m;
    }
}
