package fr.dush.mediamanager.business.mediatech.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.api.Assertions.assertThat;

/** DEAD CODE */
public abstract class YoutubeArtRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtDownloaderImplTest.class);

    @InjectMocks
    private ArtDownloaderImpl artDownloader;

    @Test
    @Ignore("Long test (depends on connection ;)")
    public void testTrailer() throws Exception {
        // http://vimeo.com/27911262
//        final String trailer = artDownloader.storeTrailer(new URL("http://www.youtube.com/watch?v=aHjpOzsQ9YI"), null);
//
//        final File file = artDownloader.getTrailerPath(trailer).toFile();
//        LOGGER.info("Trailer {} downloaded into : {} ", trailer, file.getAbsoluteFile());
//
//        assertThat(trailer).isNotNull();
//        assertThat(file).exists();
    }
}
