package fr.dush.mediamanager.dao.media.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.Order;
import fr.dush.mediamanager.dao.media.queries.SearchForm;
import fr.dush.mediamanager.dao.media.queries.SearchLimit;
import fr.dush.mediamanager.dao.media.queries.Seen;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.video.Movie;
import org.bson.types.ObjectId;
import org.jongo.Find;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Provide access to movies persisted in MongoDB. <br/>
 *
 * @author Thomas Duchatelle
 */
@ApplicationScoped
@Startup(superclass = IMovieDAO.class)
public class MovieDAOImpl extends AbstractDAO<Movie, ObjectId> implements IMovieDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDAOImpl.class);

    @Inject
    private ObjectMapper objectMapper;

    public MovieDAOImpl() {
        super(Movie.class);
    }

    @Override
    public List<Movie> findAll() {
        return Lists.newArrayList(getCollection().find().sort("{title : 1}").as(Movie.class));
    }

    @Override
    public void save(Movie dto) {
        saveOrUpdateMovie(dto);
    }

    @Override
    public void saveOrUpdateMovie(Movie movie) { // FIXME Change exception name...
        if (movie.getMediaIds() == null || movie.getMediaIds().isEmpty()) {
            throw new IllegalArgumentException("Movie must be identified by MediaId.");
        }

        // Force creation date
        if (movie.getCreation() == null) {
            movie.setCreation(new Date());
        }

        // Convert to DBObject, without 'id', if present.
        DBObject dbMovie = null;
        try {
            dbMovie = (DBObject) JSON.parse(objectMapper.writeValueAsString(movie));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't convert movie " + movie + " to JSON.", e);
        }

        dbMovie.removeField("_id");

        // Create update query
        final BasicDBObject updateQuery = new BasicDBObject("$set", dbMovie);

        // Complete fields
        final BasicDBObject push = new BasicDBObject();
        updateQuery.put("$addToSet", push);

        pushEach(dbMovie, push, "mediaIds", null);
        pushEach(dbMovie, push, "backdrops", null);
        pushEach(dbMovie, push, "genres", null);
        pushEach(dbMovie, push, "videoFiles", null);

        final Object trailers = dbMovie.removeField("trailers");
        if (trailers instanceof DBObject) {
            pushEach((DBObject) trailers, push, "sources", "trailers.");
            pushEach((DBObject) trailers, push, "trailers", "trailers.");

            dbMovie.put("trailers.refreshed", ((DBObject) trailers).get("refreshed"));
        }

        // Fields created once
        final BasicDBObject setOnInsert = new BasicDBObject();
        updateQuery.put("$setOnInsert", setOnInsert);

        setOnInsert.put("creation", dbMovie.removeField("creation"));
        if (movie.getSeen() > 0) {
            setOnInsert.put("seen", dbMovie.removeField("seen"));
        }

        // ** Update query
        getCollection().update(" {mediaIds : {$in : #}}", movie.getMediaIds()).multi().upsert().with(updateQuery.toString());
    }

    private void pushEach(DBObject dbMovie, DBObject push, String key, String keyPrefix) {
        if (isBlank(keyPrefix)) {
            keyPrefix = "";
        }

        final Object value = dbMovie.removeField(key);
        if (null != value) {
            push.put(keyPrefix + key, new BasicDBObject("$each", value));
        }
    }

    @Override
    public void incrementViewCount(ObjectId id, int inc) {
        getCollection().update("{_id : #}", id).with("{$inc : {seen : # } }", inc);
    }

    @Override
    public List<Movie> search(SearchForm form, SearchLimit limit, Order... orders) {
        if (form == null) {
            LOGGER.warn("Search movie request without form... Redirect to find all.");
            return findAll();
        }

        // Create query
        List<String> subQueries = new ArrayList<>();
        List<Object> args = new ArrayList<>();

        if (isNotBlank(form.getTitle())) {
            subQueries.add(" title : { $regex : # , $options : 'i' } ");
            args.add(form.getTitle());
        }
        if (form.getGenres() != null && !form.getGenres().isEmpty()) {
            subQueries.add("genres : { $all : # }");
            args.add(form.getGenres());
        }
        if (form.getSeen() != null && form.getSeen() != Seen.ALL) {
            switch (form.getSeen()) {
                case SEEN:
                    subQueries.add(" seen : { $gt : 0 } ");
                    break;

                case UNSEEN:
                    subQueries.add(" $or : [{seen : { $exists: false}}, {seen : 0}] ");
                    break;

                case ALL:
                default:
                    break;
            }
        }
        if (form.getMediaIds() != null && !form.getMediaIds().isEmpty()) {
            subQueries.add(" mediaIds : {$in : #} ");
            args.add(form.getMediaIds());
        }
        if (form.getCrewIds() != null && !form.getCrewIds().isEmpty()) {
            subQueries.add(" $or : [ {mainActors.sourceIds : {$in : #}}, {directors.sourceIds : {$in : #}} ] ");
            args.add(form.getCrewIds());
            args.add(form.getCrewIds());
        }

        // Execute it
        String queryString = "{ " + Joiner.on(", ").join(subQueries) + " }";
        LOGGER.debug("Search query : {} ; args = {}", queryString, args);
        Find query = getCollection().find(queryString, args.toArray(new Object[args.size()]));

        if (limit != null && limit.isMaxSizeDefined()) {
            query.limit(limit.getMaxSize());
        }

        if (orders.length > 0) {
            query.sort(getSort(Arrays.asList(orders)));
        }

        return Lists.newArrayList(query.as(Movie.class));
    }

    private static String getSort(List<Order> orders) {
        Collection<String> orderClause = Collections2.transform(orders, new Function<Order, String>() {

            @Override
            public String apply(Order input) {
                switch (input) {
                    case ALPHA:
                    case LIST:
                        return " title : 1  ";
                    case ALPHA_DESC:
                        return " title : -1 ";
                    case DATE:
                        return " release : 1 ";
                    case DATE_DESC:
                        return " release : -1";
                    case FIRST:
                        return " creation : 1  ";
                    case LAST:
                        return " creation : -1 ";
                    default:
                        return "";
                }
            }
        });

        return "{ " + Joiner.on(", ").join(orderClause) + " }";
    }

    @Override
    public List<Movie> findUnseen() {
        //        query.or(query.criteria("seen").equal(0), query.criteria("seen").doesNotExist());
        // FIXME Check if movies without 'seen' attribute match.
        return search(new SearchForm(Seen.UNSEEN), null, Order.LAST);
    }

    @Override
    public List<Movie> findByTitle(String name) {
        return search(new SearchForm(name), null, Order.ALPHA);
    }

    @Override
    public List<Movie> findByGenres(String... genres) {
        return search(new SearchForm(genres), null, Order.ALPHA);
    }

    @Override
    public List<Movie> findBySourceId(SourceId... sourceIds) {
        if (sourceIds == null || sourceIds.length <= 0) {
            throw new IllegalArgumentException("sourceIds must be defined");
        }

        SearchForm form = new SearchForm();
        form.setMediaIds(Sets.newHashSet(sourceIds));

        return search(form, null, Order.ALPHA);
    }

    @Override
    public List<Movie> findByCrew(SourceId... crew) {
        if (crew == null || crew.length <= 0) {
            throw new IllegalArgumentException("crew sourceIds must be defined");
        }

        SearchForm form = new SearchForm();
        form.setCrewIds(Sets.newHashSet(crew));

        return search(form, null, Order.ALPHA);
    }


    private static Function<SourceId, String> sourceId2string = new Function<SourceId, String>() {
        @Override
        public String apply(SourceId id) {
            return String.format(" { type : '%s', value : '%s' } ", id.getType(), id.getValue());
        }
    };
}
