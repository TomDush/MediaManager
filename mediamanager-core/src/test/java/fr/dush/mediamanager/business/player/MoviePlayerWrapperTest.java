package fr.dush.mediamanager.business.player;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.engine.CdiJunitTest;
import fr.dush.mediamanager.events.play.PlayerCollectorEvent;
import fr.dush.mediamanager.modulesapi.player.EmbeddedPlayer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoviePlayerWrapperTest extends CdiJunitTest {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private EventBus eventBus;

    @Mock
    private EmbeddedPlayer player1;
    @Mock
    private EmbeddedPlayer player2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(player1.isActive()).thenReturn(true);
        when(player2.isActive()).thenReturn(true);

    }

    @Test
    public void testInstanciate() throws Exception {
        // Before: no one exist
        PlayerCollectorEvent evt1 = new PlayerCollectorEvent();
        eventBus.post(evt1);
        assertThat(evt1.getPlayers()).isEmpty();

        // Create one...
        MoviePlayerWrapper wrapper = getMoviePlayerWrapper();
        assertThat(wrapper).isNotNull();
        wrapper.initialise(null, null, player1);

        // After collect 1       
        assertCollector(wrapper);

        // Create second...
        MoviePlayerWrapper wrapper2 = getMoviePlayerWrapper();
        assertThat(wrapper2).isNotNull();
        wrapper2.initialise(null, null, player2);

        assertCollector(wrapper, wrapper2);

        // MARK player 2 finished
        reset(player2);
        when(player2.isActive()).thenReturn(false);

        assertCollector(wrapper);
    }

    private MoviePlayerWrapper getMoviePlayerWrapper() {
        return applicationContext.getBean(MoviePlayerWrapper.class);
    }

    private void assertCollector(MoviePlayerWrapper... wrappers) {
        PlayerCollectorEvent evt3 = new PlayerCollectorEvent();
        eventBus.post(evt3);
        assertThat(evt3.getPlayers()).hasSize(wrappers.length).contains(wrappers);
    }
}
