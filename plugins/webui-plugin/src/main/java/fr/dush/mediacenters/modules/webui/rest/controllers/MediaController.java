package fr.dush.mediacenters.modules.webui.rest.controllers;

import com.google.common.collect.Lists;
import fr.dush.mediamanager.dao.media.IMediaDAO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Generic controls on medias...
 *
 * @author Thomas Duchatelle
 */
@Path("/medias")
public class MediaController {

    @Inject
    private IMediaDAO mediaDAO;

    @GET
    @Path("/genres.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> findAllGenres() {
        return mediaDAO.findAllGenres();
    }
}
