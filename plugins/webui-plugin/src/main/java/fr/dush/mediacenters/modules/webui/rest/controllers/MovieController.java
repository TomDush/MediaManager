package fr.dush.mediacenters.modules.webui.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediacenters.modules.webui.rest.dto.MediaPage;
import fr.dush.mediacenters.modules.webui.rest.dto.RequestFilter;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.domain.media.video.Movie;
import org.bson.types.ObjectId;
import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

@RequestScoped
@Path("/")
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Inject
    private IMovieDAO movieDAO;

    @Inject
    private ObjectMapper objectMapper;

    @GET
    @Path("movies")
    public String size() {
        LOGGER.info("Object mapper is : {}", objectMapper);
        return String.format("<h1>Welcome in REST Service</h1><p>There are <b>%d</b> movies in database.</p>",
                             movieDAO.findAll().size());
    }

    @GET
    @Path("/movies/{order:\\w+}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public MediaPage findMovies(@Form RequestFilter filter) {
        LOGGER.debug("Search movies with filter : {}", filter);
        // http://localhost:8080/api/movies/LAST?title=Hello%20World&genres=Action,Fantasy,Action&pagination.index=42&pagination.pageSize=12

        List<Movie> movies = movieDAO.findAll();

        // FIXME : page number is false...
        return new MediaPage(1, 20, movies.size() / 20 + 1, movies.size(), movies);
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

    public Date getDate() {
        return new Date();
    }
}
