package fr.dush.mediamanager.plugins.webui.rest.controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.eventbus.EventBus;

import fr.dush.mediamanager.events.mediatech.MovieAdminEvent;
import fr.dush.mediamanager.events.mediatech.Operation;

/** Forward admin request to CDI event bus. */
@Named
@Path("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Inject
    private EventBus eventBus;

    @Path("{type:\\w+}/{id}/{action:\\w+}")
    @GET
    public String forwardAdminRequest(@PathParam("type") String type, @PathParam("action") String action,
            @PathParam("id") String id, @QueryParam("value") String value) {

        try {
            if ("movie".equalsIgnoreCase(type)) {
                eventBus.post(new MovieAdminEvent(this, Operation.valueOf(action), id, value));

            }
            else {
                LOGGER.warn("Can forward admin request: type {} isn't supported.", type);
                return "{result: 0}";
            }

            return "{result: 1}";

        }
        catch (Exception e) {
            LOGGER.error("Can't forward admin request: {}", e.getMessage(), e);
            return "{result: 0}";
        }
    }

}
