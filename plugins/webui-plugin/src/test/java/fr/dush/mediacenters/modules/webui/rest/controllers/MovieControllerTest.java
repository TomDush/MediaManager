package fr.dush.mediacenters.modules.webui.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediacenters.modules.webui.rest.dto.MediaPage;
import fr.dush.mediacenters.modules.webui.rest.dto.Pagination;
import fr.dush.mediacenters.modules.webui.rest.dto.RequestFilter;
import fr.dush.mediacenters.modules.webui.tools.DozerMapperFactory;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.*;
import fr.dush.mediamanager.domain.media.video.Movie;
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

import java.util.ArrayList;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class MovieControllerTest {

    @InjectMocks
    private MovieController movieController;

    @Mock
    private IMovieDAO movieDAO;

    /** JSON Converter */
    @Mock
    private ObjectMapper objectMapper;

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
                             any(Order.class))).thenReturn(new PaginatedList<Movie>() {{
            setList(new ArrayList<Movie>());
        }});

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
        MediaPage movies = movieController.findMovies(filter);

        // Asserts
        verify(movieDAO).search(argSearchForm("Ironman", Seen.UNSEEN, "Action", "Comedy", "Thriller"),
                                argSearchLimit(12, 42, 30),
                                eq(Order.ALPHA));
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
                description.appendText(new SearchForm(genres) {{
                    setTitle(title);
                    setSeen(unseen);
                }}.toString());
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
                description.appendText(new SearchLimit() {{
                    setIndex(index);
                    setMaxSize(maxSize);
                    setPageSize(pageSize);
                }}.toString());
            }
        });
    }
}
