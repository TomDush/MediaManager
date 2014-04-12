package fr.dush.mediamanager.dao.media.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.*;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
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
        return newArrayList(getCollection().find().sort("{title : 1}").as(Movie.class));
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
        getCollection().update(" {mediaIds : {$in : #}}", movie.getMediaIds())
                       .multi()
                       .upsert()
                       .with(updateQuery.toString());
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
    public void markAsViewed(ObjectId id) {
        getCollection().update("{_id : #, $or: [ {seen: {$exists: 0}}, {seen : {$lt: 1}} ] }", id)
                       .with("{$set: {seen: 1}}");
    }

    @Override
    public void markAsUnViewed(ObjectId id) {
        getCollection().update("{_id : #}", id).with("{$set: {seen: 0}}");
    }

    @Override
    public PaginatedList<Movie> search(SearchForm form, SearchLimit limit, Order... orders) {
        LOGGER.info("Search movies: form={} , limit={} , orders={}", form, limit, orders);

        // Create query
        List<Object> args = new ArrayList<>();
        String queryString = generateQuery(form, args);

        // Prepare query as object + limit + order
        LOGGER.debug("Search query : {} ; args = {}", queryString, args);
        PaginatedList<Movie> result = new PaginatedList<>();
        Find query = getCollection().find(queryString, args.toArray(new Object[args.size()]));

        List<Order> orderList = newArrayList(orders);

        if (orderList.size() == 1 && orderList.contains(Order.RANDOM)) {
            // RANDOM result ...
            List<Movie> ids = newArrayList(query.projection("{_id : 1}").as(Movie.class));

            result.setFullSize(ids.size());
            result.setMaxSize(limit == null || !limit.isMaxSizeDefined() ? 10 : limit.getMaxSize());
            while (result.getList().size() < result.getMaxSize()) {
                int index = (int) Math.floor(Math.random() * ids.size());
                result.getList().add(getCollection().findOne(ids.remove(index).getId()).as(Movie.class));
            }
        } else {

            // Limit result...
            limitQuery(query, limit, result);

            // Order it
            if (orderList.isEmpty()) {
                orderList.add(Order.LIST);
            }
            query.sort(getSort(orderList));

            // Execute...
            result.setList(newArrayList(query.as(Movie.class)));

            // If element has been limited, get original size
            if (limit != null && (limit.isMaxSizeDefined() && result.getList().size() >= limit.getMaxSize() ||
                                  limit.isPaginationActive())) {

                result.setFullSize(getCollection().count(queryString, args.toArray(new Object[args.size()])));
            }
        }

        return result;
    }

    private void limitQuery(Find query, SearchLimit limit, PaginatedList<Movie> result) {
        if (limit != null) {
            if (limit.isPaginationActive()) {
                result.setSkipped(limit.getPageSize() * (limit.getIndex() - 1));
                result.setMaxSize(limit.getPageSize());

                query.skip(result.getSkipped()).limit(result.getMaxSize());
            } else if (limit.isMaxSizeDefined()) {
                result.setMaxSize(limit.getMaxSize());
                query.limit(result.getMaxSize());
            }
        }
    }

    private String generateQuery(SearchForm form, List<Object> args) {
        if (form == null) {
            return "{}";
        }

        List<String> subQueries = new ArrayList<>();
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
        if (!form.getNotNullFields().isEmpty()) {
            subQueries.addAll(Collections2.transform(form.getNotNullFields(), mediaFieldConverter()));
        }

        return "{ " + Joiner.on(", ").join(subQueries) + " }";
    }

    /**
     * Convert a MediaField to MongoDB query
     */
    private Function<MediaField, String> mediaFieldConverter() {
        return new Function<MediaField, String>() {
            @Override
            public String apply(MediaField input) {
                switch (input) {
                    case BACKDROPS:
                        return "backdrops: {$exists: true, $ne: []} ";
                    case OVERVIEW:
                        return "overview: {$type: 2, $ne: \"\"} ";
                    case POSTER:
                        return "poster: {$type: 2, $ne: \"\"} ";
                    case TRAILER:
                        return "trailers.trailers: {$exists: true, $ne: []} ";
                    case YEAR:
                        return "release: {$type: 9, $ne: \"\"} ";
                    default:
                        LOGGER.info("MovieField {} isn't known.", input);
                        return "";
                }
            }
        };
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
        return search(new SearchForm(Seen.UNSEEN), null, Order.LAST).getList();
    }

    @Override
    public List<Movie> findByTitle(String name) {
        return search(new SearchForm(name), null, Order.ALPHA).getList();
    }

    @Override
    public List<Movie> findByGenres(String... genres) {
        return search(new SearchForm(genres), null, Order.ALPHA).getList();
    }

    @Override
    public List<Movie> findBySourceId(SourceId... sourceIds) {
        if (sourceIds == null || sourceIds.length <= 0) {
            throw new IllegalArgumentException("sourceIds must be defined");
        }

        SearchForm form = new SearchForm();
        form.setMediaIds(newHashSet(sourceIds));

        return search(form, null, Order.ALPHA).getList();
    }

    @Override
    public List<Movie> findByCrew(SourceId... crew) {
        if (crew == null || crew.length <= 0) {
            throw new IllegalArgumentException("crew sourceIds must be defined");
        }

        SearchForm form = new SearchForm();
        form.setCrewIds(newHashSet(crew));

        return search(form, null, Order.ALPHA).getList();
    }

    private static Function<SourceId, String> sourceId2string = new Function<SourceId, String>() {
        @Override
        public String apply(SourceId id) {
            return String.format(" { type : '%s', value : '%s' } ", id.getType(), id.getValue());
        }
    };
}
