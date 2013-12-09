package fr.dush.mediacenters.modules.webui.rest.controllers;

import com.google.common.collect.Lists;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Generic control on medias...
 *
 * @author Thomas Duchatelle
 */
@Path("/medias")
public class MediaController {

    @GET
    @Path("/genres.json")
    public List<String> findAllGenres() {
        // TODO Something like that : db.Movies.find({}, {genres : 1, _id:0})
        return Lists.newArrayList("Action", "Crime", "Thriller", "Mystery", "Drama", "Horror", "Foreign",
                "Science Fiction", "Adventure", "War", "History", "Crime", "Fantasy", "Eastern", "Comedy", "Mystery");
    }
}
