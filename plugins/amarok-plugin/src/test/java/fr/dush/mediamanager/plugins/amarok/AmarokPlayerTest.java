package fr.dush.mediamanager.plugins.amarok;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Thomas Duchatelle
 */
public abstract class AmarokPlayerTest {

    @Test
    public void testControl() throws Exception {
        AmarokPlayer player = new AmarokPlayer();

        assertThat(player.isActive()).isTrue();

        player.play();
        assertThat(player.isPaused()).isFalse();
        player.pause();
        assertThat(player.isPaused()).isTrue();
        player.pause();
        assertThat(player.isPaused()).isFalse();

    }
}
