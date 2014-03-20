package fr.dush.mediamanager.plugins.jmplayer;

import org.junit.Test;

import java.io.File;

/**
 * @author Thomas Duchatelle
 */
public class JMPlayerTest {

    public static final String FILE =
            "/mnt/data/Films/Sagas/Transformers/Transformers.Dark.Of.The.Moon.2011.FRENCH.DVDRip.XviD-AYMO.CD1.avi";

    @Test
    public void testJMPlayer() throws Exception {
        JMPlayer player = new JMPlayer();
        player.open(new File(FILE));

        while (player.isPlaying()) {
            Thread.sleep(1000);
        }

    }
}
