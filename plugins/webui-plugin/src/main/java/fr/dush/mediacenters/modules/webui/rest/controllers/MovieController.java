package fr.dush.mediacenters.modules.webui.rest.controllers;

import fr.dush.mediacenters.modules.webui.rest.dto.MediaPage;
import fr.dush.mediacenters.modules.webui.rest.dto.RequestFilter;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.PaginatedList;
import fr.dush.mediamanager.dao.media.queries.SearchForm;
import fr.dush.mediamanager.dao.media.queries.SearchLimit;
import fr.dush.mediamanager.domain.media.video.Movie;
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

@RequestScoped
@Path("/")
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Inject
    private IMovieDAO movieDAO;

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
        MediaPage mediaPage = new MediaPage(movies.getList());

        if (filter.getPagination() != null && filter.getPagination().getIndex() > 0) {
            mediaPage.setPageSize(filter.getPagination().getPageSize());
            mediaPage.setPage(filter.getPagination().getIndex());
        }

        mediaPage.setSize(movies.getFullSize());

        LOGGER.debug("Return {} movies for request {}", mediaPage.getElements().size(), filter);
        return mediaPage;
    }

    @GET
    @Path("/movie/{id}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Movie findById(@PathParam("id") ObjectId id) {
        Movie movie = movieDAO.findById(id);
        if (movie != null) {
            return movie;
        }

        throw new WebApplicationException(id + " not found...", HttpURLConnection.HTTP_NOT_FOUND);
    }
}
