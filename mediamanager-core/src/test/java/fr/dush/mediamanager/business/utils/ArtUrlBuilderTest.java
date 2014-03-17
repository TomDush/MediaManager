package fr.dush.mediamanager.business.utils;

import fr.dush.mediamanager.domain.media.art.ArtType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Thomas Duchatelle
 */
public class ArtUrlBuilderTest {

    public static final String ART_REF = "poster/themoviedb/ironman-1/posterqbcdef12465.jpg";

    @Test
    public void testCleanRefUrl() throws Exception {
        assertThat(ArtUrlBuilder.cleanUrl(" [It's a_complex- id! here] ")).isEqualTo("it-s-a-complex-id-here");

    }

    @Test
    public void testOtherStaticMethod() throws Exception {
        assertThat(ArtUrlBuilder.readType(ART_REF)).isEqualTo(ArtType.POSTER);

        assertThat(ArtUrlBuilder.getArtRepositoryId(ART_REF)).isEqualTo("themoviedb");
    }
}
