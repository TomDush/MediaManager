package fr.dush.mediamanager.business.mediatech;

import static org.fest.assertions.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.engine.mock.MockedConfiguration;

@RunWith(BlockJUnit4ClassRunner.class)
public class ArtDownloaderImplTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtDownloaderImplTest.class);

	@InjectMocks
	private ArtDownloaderImpl artDownloader;

	@Spy
	private ModuleConfiguration configuration = new MockedConfiguration("downloader.imagespath", "target/", "downloader.trailerpath",
			"target/");

	@Before
	public void initMockito() throws IOException {
		MockitoAnnotations.initMocks(this);
		artDownloader.readConfiguration();
	}

	@Test
	@Ignore("Long test (depends on connection ;)")
	public void testDownloadFile() throws Exception {
		final String image = artDownloader.storeImage(new URL(
				"http://d3gtl9l2a4fn1j.cloudfront.net/t/p/original/bgSHbGEA1OM6qDs3Qba4VlSZsNG.jpg"), null);

		final File file = artDownloader.getImagePath(image).toFile();
		LOGGER.info("Image {} downloaded into : {} ", image, file.getAbsoluteFile());

		assertThat(image).isNotNull();
		assertThat(file).exists();
	}

	@Test
	@Ignore("Long test (depends on connection ;)")
	public void testTrailer() throws Exception {
		// http://vimeo.com/27911262
		final String trailer = artDownloader.storeTrailer(new URL("http://www.youtube.com/watch?v=aHjpOzsQ9YI"), null);

		final File file = artDownloader.getTrailerPath(trailer).toFile();
		LOGGER.info("Trailer {} downloaded into : {} ", trailer, file.getAbsoluteFile());

		assertThat(trailer).isNotNull();
		assertThat(file).exists();
	}

	@Test
	public void testGetSimpleName() throws Exception {
		assertThat(ArtDownloaderImpl.getSimpleFileName("Crystallize - Lindsey Stirling (Dubstep Violin Original Song).webm")).isEqualTo(
				"Crystallize_Lindsey_Stirling");
	}
}
