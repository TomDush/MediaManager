package fr.dush.mediamanager.plugins.webui.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.*;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.*;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.plugins.webui.rest.dto.MediaPage;
import fr.dush.mediamanager.plugins.webui.rest.dto.MovieDTO;
import fr.dush.mediamanager.plugins.webui.rest.dto.Pagination;
import fr.dush.mediamanager.plugins.webui.rest.dto.RequestFilter;
import fr.dush.mediamanager.tools.DozerMapperFactory;
import fr.dush.mediamanager.tools.JsonConverterProducer;
import org.bson.types.ObjectId;
import org.dozer.Mapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.google.common.collect.Lists.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class MovieControllerTest {

    public static final String MOVIE_ID = "5240760958eff5a9e1d18203";
    @InjectMocks
    private MovieController movieController;

    @Mock
    private IMovieDAO movieDAO;

    /** JSON Converter */
    @Spy
    private ObjectMapper objectMapper = new JsonConverterProducer().produceObjectMapper();
    @Mock
    private IRecoveryDAO recoveryDAO;

    /** Bean to bean mapper */
    @Spy
    private Mapper dozerMapper = new DozerMapperFactory().getDozerMapper();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindMovies() throws Exception {
        when(movieDAO.search(any(SearchForm.class),
                             any(SearchLimit.class),
                             any(Order.class))).thenReturn(new PaginatedList<Movie>() {

            {
                setList(newArrayList(newMovie()));
            }
        });

        RequestFilter filter = new RequestFilter();
        filter.setGenres("Action,Comedy,Thriller");
        filter.setOrder("ALPHA");
        filter.setSeen("UNSEEN");
        filter.setSize(42);
        filter.setTitle("Ironman");

        filter.setPagination(new Pagination());
        filter.getPagination().setIndex(12);
        filter.getPagination().setPageSize(30);

        // Exec
        MediaPage<MediaSummary> movies = movieController.findMovies(filter);

        // Asserts
        verify(movieDAO).search(argSearchForm("Ironman", Seen.UNSEEN, "Action", "Comedy", "Thriller"),
                                argSearchLimit(12, 42, 30),
                                eq(Order.ALPHA));

        assertThat(movies.getElements()).hasSize(1);
        assertThat(movies.getElements().get(0).getId()).isEqualTo(MOVIE_ID);

    }

    private Movie newMovie() {
        Movie movie = new Movie();
        movie.setId(new ObjectId(MOVIE_ID));
        movie.setTitle("Ironman 3");

        return movie;
    }

    @Test
    public void testGetMovie() throws Exception {
        Movie movie = new Movie();
        movie.setId(new ObjectId(MOVIE_ID));
        movie.setTitle("Ironman 3");

        when(movieDAO.findById(new ObjectId(MOVIE_ID))).thenReturn(movie);
        Recovery recovery = new Recovery(new MediaSummary(MediaType.MOVIE, MOVIE_ID));
        recovery.setPosition(42);
        when(recoveryDAO.findById(any(MediaReference.class))).thenReturn(recovery);

        MovieDTO dto = movieController.findById(new ObjectId(MOVIE_ID));
        assertThat(dto.getId()).isNotNull().isEqualTo(new ObjectId(MOVIE_ID));
        assertThat(dto.getAliasId()).isNotNull().isEqualTo(new ObjectId(MOVIE_ID));

        objectMapper.writeValueAsString(dto); // Test conversion works.
    }

    private static SearchForm argSearchForm(final String title, final Seen unseen, final String... genres) {
        return argThat(new BaseMatcher<SearchForm>() {

            @Override
            public boolean matches(Object o) {
                if (o instanceof SearchForm) {
                    Assertions.assertThat((SearchForm) o).hasGenres(genres).hasSeen(unseen).hasTitle(title);

                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(new SearchForm(genres) {

                    {
                        setTitle(title);
                        setSeen(unseen);
                    }
                }.toString());
            }
        });
    }

    private static SearchLimit argSearchLimit(final int index, final int maxSize, final int pageSize) {
        return argThat(new BaseMatcher<SearchLimit>() {

            @Override
            public boolean matches(Object o) {
                if (o instanceof SearchLimit) {
                    Assertions.assertThat((SearchLimit) o).hasIndex(index).hasMaxSize(maxSize).hasPageSize(pageSize);

                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(new SearchLimit() {

                    {
                        setIndex(index);
                        setMaxSize(maxSize);
                        setPageSize(pageSize);
                    }
                }.toString());
            }
        });
    }
}
