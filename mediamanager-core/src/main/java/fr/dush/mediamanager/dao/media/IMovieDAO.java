package fr.dush.mediamanager.dao.media;

import java.util.List;

import org.bson.types.ObjectId;

import fr.dush.mediamanager.dao.IDao;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.video.Movie;

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
	 * Find movies by IDs provided by sources (imdb, movieDb, ...)
	 *
	 * @param sourceIds
	 * @return
	 */
	List<Movie> findBySourceId(SourceId... sourceIds);

	/**
	 * Movies never seen yet.
	 *
	 * @return Movies ordered by creation in database
	 */
	List<Movie> findUnseen();

	/**
	 * Find by approximative name
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