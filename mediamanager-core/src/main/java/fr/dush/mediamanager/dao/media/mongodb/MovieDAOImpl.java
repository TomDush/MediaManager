package fr.dush.mediamanager.dao.media.mongodb;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.base.Function;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.video.Movie;

/**
 * Provide access to movies persisted in MongoDB. <br/>
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
@Startup(superclass = IMovieDAO.class)
public class MovieDAOImpl extends AbstractDAO<Movie, ObjectId> implements IMovieDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(MovieDAOImpl.class);

	public MovieDAOImpl() {
		super(Movie.class);
	}

	@Override
	public void save(Movie dto) {
		saveOrUpdateMovie(dto);
	}

	@Override
	public void saveOrUpdateMovie(Movie movie) {
		// Force creation date
		if (null == movie.getCreation()) {
			movie.setCreation(new Date());
		}

		// Convert to DBObject, without 'id', if present.
		final DBObject dbMovie = getMapper().toDBObject(movie);
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
		if (movie.getSeen() > 0) setOnInsert.put("seen", dbMovie.removeField("seen"));

		// ** Update query
		final QueryImpl<Movie> query = (QueryImpl<Movie>) createQueryOnIds(movie.getMediaIds());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Update movies matching query : {}", query);
			LOGGER.debug("Update query is : {}", updateQuery);
		}

		getDs().getCollection(Movie.class).update(query.getQueryObject(), updateQuery, true, true);

	}

	private void pushEach(DBObject dbMovie, DBObject push, String key, String keyPrefix) {
		if (isBlank(keyPrefix)) keyPrefix = "";

		final Object value = dbMovie.removeField(key);
		if (null != value) push.put(keyPrefix + key, new BasicDBObject("$each", value));
	}

	@Override
	public void incrementViewCount(Movie movie, int inc) {
		final UpdateOperations<Movie> updateOp = createUpdateOperations().inc("seen", inc);

		if (null != movie.getId()) {
			// Classic update (by ID)
			getDs().update(movie, updateOp);

		} else {
			final Query<Movie> query = createQueryOnIds(movie.getMediaIds());

			getDs().update(query, updateOp);
		}
	}

	@Override
	public List<Movie> findBySourceId(SourceId... sourceIds) {
		if (null == sourceIds || sourceIds.length <= 0) {
			throw new IllegalArgumentException("sourceIds must be defined");
		}

		final Query<Movie> query = createQueryOnIds(Arrays.asList(sourceIds));

		return query.order("title").asList();
	}

	private Query<Movie> createQueryOnIds(Collection<SourceId> mediaIds) {
		final Query<Movie> query = createQuery();
		query.criteria("mediaIds").in(mediaIds);
		return query;
	}

	@Override
	public List<Movie> findUnseen() {
		final Query<Movie> query = createQuery();
		query.or(query.criteria("seen").equal(0), query.criteria("seen").doesNotExist());

		return query.order("-creation").asList();
	}

	@Override
	public List<Movie> findByTitle(String name) {
		return createNativeQuery("{ title : { $regex : '%s' , $options : 'i' } }", name).order("title").asList();
	}

	@Override
	public List<Movie> findByGenres(String... genres) {
		final Query<Movie> query = createNativeQuery("{ genres : { $all : %s } }", asJsonList(genres));

		return query.order("title").asList();
	}

	@Override
	public List<Movie> findByCrew(SourceId... crew) {
		StringBuilder sb = new StringBuilder("{ $or : [ ");
		sb.append(" { 'mainActors.sourceIds' : { $in : %s } },");
		sb.append(" { 'directors.sourceIds' : { $in : %s } }");
		sb.append("] }");

		final String ids = asJsonList(crew, sourceId2string);

		// Morphia native query
		return createNativeQuery(sb.toString(), ids, ids).order("title").asList();
	}

	@Override
	public List<Movie> findAll() {
		return createQuery().order("title").asList();
	}

	private static Function<SourceId, String> sourceId2string = new Function<SourceId, String>() {
		@Override
		public String apply(SourceId id) {
			return String.format(" { type : '%s', value : '%s' } ", id.getType(), id.getValue());
		}
	};
}
