package fr.dush.mediamanager.dao.media.mongodb;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fr.dush.mediamanager.dao.media.IMediaDAO;
import fr.dush.mediamanager.domain.media.video.Movie;
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

        // "{ $unwind : '$genres' }").and(
        //        "{ $group : {_id : 1, genres : {$addToSet : {$each : '$genres'}} } }"
        List<List<String>> genres = movies.aggregate("{ $unwind : '$genres' }").and("{ $group : {_id : '1', " +
                "genres : {$addToSet : '$genres'}} }").map(new ResultHandler<List<String>>() {
            @Override
            public List<String> map(DBObject dbObject) {
                LOGGER.info("Map object : {} [class = {} )", dbObject, dbObject.get("genres").getClass());
                return (List<String>) dbObject.get("genres");
            }
        });

        LOGGER.info("Found genres : {}", genres);

        if (genres == null || genres.isEmpty()) {
            LOGGER.error("Couldn't get genres list...");
            return new HashSet<>();
        }

        return new HashSet<>(genres.get(0));
    }
}
