package fr.dush.mediamanager.dao.media;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.dush.mediamanager.dao.media.queries.Order;
import fr.dush.mediamanager.dao.media.queries.SearchForm;
import fr.dush.mediamanager.dao.media.queries.SearchLimit;
import org.bson.types.ObjectId;

import fr.dush.mediamanager.dao.IDao;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.video.Movie;

/**
 * Interface to movies saved in database
 *
 * @author Thomas Duchatelle
 *
 */
public interface IMovieDAO extends IDao<Movie, ObjectId> {

	/**
     * Save or update movie, based on {@link Movie#getMediaIds()}.
	 *
     * @param movie
     */
	void saveOrUpdateMovie(Movie movie);

	/**
	 * Increment {@link Movie#getSeen()} by inc.
	 *
     * @param id
     * @param inc Number of view to add
     */
	void incrementViewCount(ObjectId id, int inc);

	/**
	 * Find movies by IDs provided by sources (imdb, movieDb, ...)
	 *
	 * @param sourceIds
	 * @return
	 */
	List<Movie> findBySourceId(SourceId... sourceIds);

    /** Search movies by multiple criteria */
    List<Movie> search(SearchForm form, SearchLimit limit, Order... order);

	/**
	 * Movies never seen yet.
	 *
	 * @return Movies ordered by creation in database
	 */
	List<Movie> findUnseen();

	/**
	 * Find by approximate name
	 *
	 * @param name
	 * @return Movies matching name
	 */
	List<Movie> findByTitle(String name);

	/**
	 * Find movies by genre.
	 *
	 * @param genres
	 * @return Movies matching all genre.
	 */
	List<Movie> findByGenres(String... genres);

	/**
	 * Find movies where person(s) are part of crew.
	 *
	 * @param crew Person, identified by their source ID
	 * @return Movie matching one or more persons.
	 */
	List<Movie> findByCrew(SourceId... crew);

}