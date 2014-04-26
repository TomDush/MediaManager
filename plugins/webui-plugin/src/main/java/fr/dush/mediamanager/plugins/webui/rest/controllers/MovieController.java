package fr.dush.mediamanager.plugins.webui.rest.controllers;

import com.google.common.base.Function;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.PaginatedList;
import fr.dush.mediamanager.dao.media.queries.SearchForm;
import fr.dush.mediamanager.dao.media.queries.SearchLimit;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.domain.media.MediaSummary;
import fr.dush.mediamanager.domain.media.Recovery;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.plugins.webui.rest.dto.MediaPage;
import fr.dush.mediamanager.plugins.webui.rest.dto.MovieDTO;
import fr.dush.mediamanager.plugins.webui.rest.dto.RecoveryDTO;
import fr.dush.mediamanager.plugins.webui.rest.dto.RequestFilter;
import org.bson.types.ObjectId;
import org.dozer.Mapper;
import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.HttpURLConnection;

import static com.google.common.collect.Lists.*;

@RequestScoped
@Path("/")
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Inject
    private IMovieDAO movieDAO;
    @Inject
    private IRecoveryDAO recoveryDAO;

    /** Mapper bean to bean */
    @Inject
    private Mapper dozerMapper;

    @GET
    @Path("movies")
    public String home() {
        return String.format("<h1>Welcome in REST Service</h1><p>There are <b>%d</b> movies in database.</p>",
                             movieDAO.findAll().size());
    }

    @GET
    @Path("/movies/{order:\\w+}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public MediaPage findMovies(@Form RequestFilter filter) {
        LOGGER.info("Search movies with filter : {}", filter);

        // Exec request
        PaginatedList<Movie> movies = movieDAO.search(dozerMapper.map(filter, SearchForm.class),
                                                      dozerMapper.map(filter, SearchLimit.class),
                                                      filter.getOrder());

        // Create request result
        MediaPage mediaPage = new MediaPage(transform(movies.getList(), new Function<Movie, MediaSummary>() {
            @Override
            public MediaSummary apply(Movie input) {
                return dozerMapper.map(input, MediaSummary.class);
            }
        }));

        if (filter.getPagination() != null && filter.getPagination().getIndex() > 0) {
            mediaPage.setPageSize(filter.getPagination().getPageSize());
            mediaPage.setPage(filter.getPagination().getIndex());
        }

        mediaPage.setSize(movies.getFullSize());

        // TODO Disable pagination if all movies has been found.
        LOGGER.debug("Return {} movies for request {}", mediaPage.getElements().size(), filter);
        return mediaPage;
    }

    @GET
    @Path("/movie/{id}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public MovieDTO findById(@PathParam("id") ObjectId id) {
        try {
            Movie movie = movieDAO.findById(id);
            if (movie == null) {
                throw new WebApplicationException(id + " not found...", HttpURLConnection.HTTP_NOT_FOUND);
            }

            MovieDTO dto = dozerMapper.map(movie, MovieDTO.class);

            // Recovering?
            Recovery recovery =
                    recoveryDAO.findById(new MediaReference(fr.dush.mediamanager.domain.media.MediaType.MOVIE,
                                                            movie.getId()));
            if (recovery != null) {
                dto.setRecovery(dozerMapper.map(recovery, RecoveryDTO.class));
            }

            return dto;

        } catch (Exception e) {
            LOGGER.error("Could not find movie with id [{}].", id, e);
            throw new WebApplicationException(id + " not found...", HttpURLConnection.HTTP_NOT_FOUND);
        }

    }
}
