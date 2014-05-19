package fr.dush.mediamanager.plugins.enrich;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.scan.MoviesParsedName;
import fr.dush.mediamanager.modulesapi.enrich.FindTrailersEvent;
import fr.dush.mediamanager.plugins.enrich.moviesdb.TheMovieDBProvider;

@RunWith(BlockJUnit4ClassRunner.class)
public class TheMovieDbEnricherTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbEnricherTest.class);

    @InjectMocks
    private TheMovieDbEnricher enrichMedia;

    @Spy
    private TheMovieDbApi api;

    @Before
    public void initTest() throws MovieDbException {
        final TheMovieDBProvider theMovieDBProvider = new TheMovieDBProvider();
        theMovieDBProvider.setConfiguration(new ModuleConfiguration(null, new FieldSet()));
        api = theMovieDBProvider.provideTheMovieDbApi();

        MockitoAnnotations.initMocks(this);

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
