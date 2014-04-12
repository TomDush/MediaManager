package fr.dush.mediacenters.modules.webui.rest.controllers;

import fr.dush.mediamanager.events.mediatech.AdminEvent;
import fr.dush.mediamanager.events.mediatech.MovieAdminEvent;
import fr.dush.mediamanager.events.mediatech.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/** Forward admin request to CDI event bus. */
@RequestScoped
@Path("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Inject
    private Event<AdminEvent> adminBus;

    @Path("{type:\\w+}/{id}/{action:\\w+}")
    @GET
    public String forwardAdminRequest(@PathParam("type") String type, @PathParam("action") String action, @PathParam(
            "id") String id, @QueryParam("value") String value) {

        try {
            if ("movie".equalsIgnoreCase(type)) {
                adminBus.fire(new MovieAdminEvent(this, Operation.valueOf(action), id, value));

            } else {
                LOGGER.warn("Can forward admin request: type {} isn't supported.", type);
                return "{result: 0}";
            }

            return "{result: 1}";

        } catch (Exception e) {
            LOGGER.error("Can't forward admin request: {}", e.getMessage(), e);
            return "{result: 0}";
        }
    }

}
