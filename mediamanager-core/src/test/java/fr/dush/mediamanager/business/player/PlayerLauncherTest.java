package fr.dush.mediamanager.business.player;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.VideoFile;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.events.play.PlayRequestEvent;
import fr.dush.mediamanager.modulesapi.player.EmbeddedPlayer;
import fr.dush.mediamanager.modulesapi.player.PlayerProvider;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

import static com.google.common.collect.Lists.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Thomas Duchatelle
 */
public class PlayerLauncherTest extends SimpleJunitTest {

    @InjectMocks
    private PlayerLauncher playerLauncher;

    @Mock
    private IMovieDAO movieDAO;
    @Mock
    private IModulesManager modulesManager;

    @Mock
    private PlayerProvider aviProvider;
    @Mock
    private PlayerProvider mp4Provider;

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private EmbeddedPlayer player;

    @Mock
    private EventBus bus;

    @Before
    public void setUp() throws Exception {
        // Movie DAO
        Movie movie = new Movie();
        movie.getVideoFiles().add(new VideoFile(Paths.get("/it/the/first/video.AVI")));

        VideoFile videoFile = new VideoFile(Paths.get("/highQualityVideo_CD1.mp4"));
        videoFile.getNextParts().add("/highQualityVideo_CD2.mp4");
        movie.getVideoFiles().add(videoFile);

        when(movieDAO.findById(any(ObjectId.class))).thenReturn(movie);

        // Providers
        when(aviProvider.managedExtensions()).thenReturn(newArrayList("AvI"));
        when(mp4Provider.managedExtensions()).thenReturn(newArrayList("MP4"));

        when(aviProvider.createPlayerInstance()).thenReturn(player);
        when(mp4Provider.createPlayerInstance()).thenReturn(player);

        // playerLauncher providers
        HashSet<PlayerProvider> providers = new HashSet<PlayerProvider>();
        providers.add(aviProvider);
        providers.add(mp4Provider);

        when(modulesManager.findModuleByType(PlayerProvider.class)).thenReturn(providers);

        playerLauncher.loadProviders();

        // Movie wrapper factory
        MoviePlayerWrapper wrapper = new MoviePlayerWrapper();
        wrapper.setBusEvent(bus);
        when(applicationContext.getBean(MoviePlayerWrapper.class)).thenReturn(wrapper);
    }

    @Test
    public void testSimpleFile() throws Exception {
        playerLauncher.playMedia(new PlayRequestEvent(MediaType.MOVIE,
                                                      "53277c9eef5bc47460a18ad0",
                                                      "/it/the/first/video.AVI"));

        verify(aviProvider).createPlayerInstance();
        ArrayList<Path> paths = new ArrayList<Path>();
        paths.add(Paths.get("/it/the/first/video.AVI"));
        verify(player).play(paths);

    }

    @Test
    public void testMultipleFile() throws Exception {
        playerLauncher.playMedia(new PlayRequestEvent(MediaType.MOVIE,
                                                      "53277c9eef5bc47460a18ad0",
                                                      "/highQualityVideo_CD1.mp4"));

        verify(mp4Provider).createPlayerInstance();
        verify(player).play(newArrayList(Paths.get("/highQualityVideo_CD1.mp4"),
                                         Paths.get("/highQualityVideo_CD2.mp4")));

    }
}
