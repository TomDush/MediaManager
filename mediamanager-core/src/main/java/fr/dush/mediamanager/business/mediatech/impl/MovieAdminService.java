package fr.dush.mediamanager.business.mediatech.impl;

import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.events.mediatech.MovieAdminEvent;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author Thomas Duchatelle
 */
@ApplicationScoped
public class MovieAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieAdminService.class);

    @Inject
    private IMovieDAO movieDAO;

    /** Listen all movies events and execute expected operation if supported */
    public void handleOperation(@Observes MovieAdminEvent event) {
        LOGGER.debug("handleOperation({})", event);

        switch (event.getOperation()) {
            case MARK_VIEWED:
                movieDAO.markAsViewed(new ObjectId(event.getId()));
                event.markHandled();
                break;
            case MARK_UNVIEWED:
                movieDAO.markAsUnViewed(new ObjectId(event.getId()));
                event.markHandled();
                break;

        }
    }

}
