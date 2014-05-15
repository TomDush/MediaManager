package fr.dush.mediamanager.dao.media.mongodb;

import com.mongodb.DBObject;
import fr.dush.mediamanager.dao.media.IMediaDAO;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Duchatelle
 */
public class MediaDAOImpl implements IMediaDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaDAOImpl.class);

    @Inject
    private Jongo jongo;

    @Override
    public Set<String> findAllGenres() {
        MongoCollection movies = jongo.getCollection("Movies");

        List<List<String>> genres = movies.aggregate("{ $unwind : '$genres' }")
                                          .and("{ $group : {_id : '1', " + "genres : {$addToSet : '$genres'}} }")
                                          .map(new ResultHandler<List<String>>() {
                                              @Override
                                              public List<String> map(DBObject dbObject) {
                                                  LOGGER.debug("Map object : {} [class = {} )",
                                                               dbObject,
                                                               dbObject.get("genres").getClass());
                                                  return (List<String>) dbObject.get("genres");
                                              }
                                          });


        if (genres == null || genres.isEmpty()) {
            LOGGER.error("Couldn't get genres list... (database empty?)");
            return new HashSet<>();
        }

        LOGGER.debug("Found genres : {}", genres);
        return new HashSet<>(genres.get(0));
    }
}
