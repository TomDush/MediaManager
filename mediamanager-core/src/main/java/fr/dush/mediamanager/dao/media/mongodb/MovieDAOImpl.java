package fr.dush.mediamanager.dao.media.mongodb;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.common.base.Function;

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
public class MovieDAOImpl extends AbstractDAO<Movie, ObjectId> implements IMovieDAO {

	public MovieDAOImpl() {
		super(Movie.class);
	}

	@Override
	public void saveOrUpdateMovie(Movie movie) {
		// Morphia test...
		save(movie);
	}

	@Override
	public List<Movie> findBySourceId(SourceId... sourceIds) {
		if (null == sourceIds || sourceIds.length <= 0) throw new IllegalArgumentException("sourceIds must be defined");

		final Query<Movie> query = createQuery();
		query.criteria("mediaIds").in(Arrays.asList(sourceIds));

		return query.order("title").asList();
	}

	@Override
	public List<Movie> findUnseen() {
		final Query<Movie> query = createQuery();
		query.criteria("seen").equal(0);

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

//		final Query<Movie> query = createQuery();
//		query.criteria("mainActors.sourceIds").hasAnyOf(Arrays.asList(crew))
//				.or(query.criteria("directors.sourceIds").hasAnyOf(Arrays.asList(crew)));
//
//		return query.order("title").asList();
	}

	private static Function<SourceId, String> sourceId2string = new Function<SourceId, String>() {
		@Override
		public String apply(SourceId id) {
			return String.format(" { type : '%s', value : '%s' } ", id.getType(), id.getValue());
		}
	};
}
