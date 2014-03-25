package fr.dush.mediamanager.plugins.jmplayer;

import fr.dush.mediamanager.events.play.PlayerEvent;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;

/**
 * @author Thomas Duchatelle
 */
@RunWith(MockitoJUnitRunner.class)
public class JMPlayerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMPlayerTest.class);

    public static final String FILE =
            "/mnt/data/Films/Sagas/Transformers/Transformers.Dark.Of.The.Moon.2011.FRENCH.DVDRip.XviD-AYMO.CD1.avi";
    //    public static final String FILE =
    //            "/mnt/unsafe/Movies/Riddick 2013 BRRip AC3 XviD-haяkš/Riddick 2013 BRRip AC3 XviD-haяkš.avi";

    public static final String MPLAYER_PATH = "/usr/bin/mplayer";
    public static final String OPT = " -fs";

    @Mock
    private Event<PlayerEvent> eventBus;

    @Before
    public void setUp() throws Exception {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LOGGER.info("Event fired: {}", invocation.getArguments()[0]);
                return null;
            }
        }).when(eventBus).fire(any(PlayerEvent.class));
    }

    @Test
    @Ignore
    public void testJMPlayer() throws Exception {
        JMPlayer player = new JMPlayer(eventBus, MPLAYER_PATH, OPT);
        player.play(Paths.get(FILE));
        player.setPosition(20 * 60);

        LOGGER.info("Time length: {}", player.getTotalLength());

        int i = 0;
        while (player.isActive()) {
            Thread.sleep(1000);

            //            System.out.println("Current: " + player.getPosition() + " paused? " + player.isPaused());
            switch (i++) {
                case 5:
                    player.pause();
                    break;
                case 9:
                    player.pause();
                    break;
                case 40:
                    player.quit();
                    break;
            }
        }

    }

    @Test
    public void testFireEvent() throws Exception {
        final Map<String, String> values = new HashMap<>();

        JMPlayer player = new JMPlayer(eventBus, MPLAYER_PATH, "");
        player.getArgReaders().add(new ArgReader("") {
            @Override
            public synchronized void doIt(String paramName, String value) {
                values.put(paramName, value);
            }
        });

        player.readMPlayerLog(Level.INFO, "ANS_LENGTH=5253.58");
        player.readMPlayerLog(Level.INFO, "ANS_TIME_POSITION=50.0");
        player.readMPlayerLog(Level.INFO, "ANS_TIME_POSITION=56.1");

        player.readMPlayerLog(Level.INFO, "A: 285.7 V: 285.7 A-V:  0.000 ct: -0.042 6851/6851  1%  1%  0.3% 0 0");

        assertThat(player.getPosition()).isEqualTo(285L);
        assertThat(values).contains(MapEntry.entry("TIME_POSITION", "56.1"), MapEntry.entry("LENGTH", "5253.58"));

        player.readMPlayerLog(Level.INFO, "=====PAUSE=====");
        assertThat(player.isPaused()).isTrue();

        player.readMPlayerLog(Level.INFO, "A: 298.1 V: 285.7 A-V:  0.000 ct: -0.042 6851/6851  1%  1%  0.3% 0 0");
        assertThat(player.isPaused()).isFalse();
        assertThat(player.getPosition()).isEqualTo(298L);

    }
}
