package fr.dush.mediamanager.business.mediatech.impl;

import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.events.mediatech.MovieAdminEvent;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Thomas Duchatelle
 */
@Named
public class MovieAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieAdminService.class);

    @Inject
    private IMovieDAO movieDAO;

    /** Listen all movies events and execute expected operation if supported */
    @Subscribe
    public void handleOperation(MovieAdminEvent event) {
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
