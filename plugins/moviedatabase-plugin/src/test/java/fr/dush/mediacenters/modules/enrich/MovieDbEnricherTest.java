package fr.dush.mediacenters.modules.enrich;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

import fr.dush.mediacenters.modules.enrich.moviesdb.TheMovieDBProvider;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.business.mediatech.scanner.MoviesParsedName;
import fr.dush.mediamanager.dto.configuration.FieldSet;
import fr.dush.mediamanager.dto.media.video.Film;
import fr.dush.mediamanager.modulesapi.enrich.FindTrailersEvent;

@RunWith(BlockJUnit4ClassRunner.class)
public class MovieDbEnricherTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MovieDbEnricherTest.class);

	@InjectMocks
	private MovieDbEnricher enrichMedia;

	@Spy
	private TheMovieDbApi api;

	@Mock
	private IArtDownloader metaMediaManager;

	@Before
	public void initTest() throws MovieDbException {
		final TheMovieDBProvider theMovieDBProvider = new TheMovieDBProvider();
		theMovieDBProvider.setConfiguration(new ModuleConfiguration(null, new FieldSet()));
		api = theMovieDBProvider.provideTheMovieDbApi();

		MockitoAnnotations.initMocks(this);

		when(metaMediaManager.storeImage(any(URL.class), anyString())).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArguments()[0].toString();
			}
		});
	}

	@Test
	public void testFindFilmData() throws Exception {
		final List<Film> list = enrichMedia.findMediaData(new MoviesParsedName("Transformers", 2007));
		assertThat(list).isNotEmpty();

		for (Film f : list) {
			enrichMedia.enrichMedia(f);
			enrichMedia.completeTrailers(new FindTrailersEvent(this, f, "en"));


			LOGGER.info("\n{}", f.prettyPrint(null));
//			LOGGER.info("Trailers : {}", enrichMedia.findTrailers(f, "en"));
		}

	}

}
