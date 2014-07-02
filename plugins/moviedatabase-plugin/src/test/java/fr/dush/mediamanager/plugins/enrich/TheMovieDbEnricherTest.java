package fr.dush.mediamanager.plugins.enrich;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.scan.MoviesParsedName;
import fr.dush.mediamanager.modulesapi.enrich.FindTrailersEvent;
import fr.dush.mediamanager.plugins.enrich.moviesdb.TheMovieDBProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class TheMovieDbEnricherTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbEnricherTest.class);

    @InjectMocks
    private TheMovieDbEnricher enrichMedia;

    private TheMovieDbApi api;

    @Mock
    private ModuleConfiguration moduleConfiguration;

    @Before
    public void initTest() throws MovieDbException {
        MockitoAnnotations.initMocks(this);

        // Module configuration behavior
        when(moduleConfiguration.readValue(eq("key"), anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[1];
            }
        });

        // Create API
        final TheMovieDBProvider theMovieDBProvider = new TheMovieDBProvider();
        theMovieDBProvider.setConfiguration(moduleConfiguration);
        api = spy(theMovieDBProvider.provideTheMovieDbApi());

        // Set dependencies
        enrichMedia.setApi(api);

    }

    @Test
    public void testFindFilmData() throws Exception {
        final List<Movie> list = enrichMedia.findMediaData(new MoviesParsedName(null, "Transformers", 2007));
        assertThat(list).isNotEmpty();

        for (Movie f : list) {
            enrichMedia.enrichMedia(f);
            enrichMedia.completeTrailers(new FindTrailersEvent(this, f, "en"));

            LOGGER.info("\n{}", f.prettyPrint(null));
            //			LOGGER.info("Trailers : {}", enrichMedia.findTrailers(f, "en"));
        }

        // Now, try to download arts...
        Movie transformer = list.get(0);
        TheMovieDBArtUrl url = new TheMovieDBArtUrl(transformer.getPoster());
        LOGGER.info("Art key = [{}]", url.getPath());
        printUrl(url, "original");
        printUrl(url, "w92");
        printUrl(url, "w185");

    }

    private void printUrl(TheMovieDBArtUrl url, String size) throws MovieDbException {
        LOGGER.info("\t- {}: {}", size, api.createImageUrl(url.getPath(), size));
    }

}
