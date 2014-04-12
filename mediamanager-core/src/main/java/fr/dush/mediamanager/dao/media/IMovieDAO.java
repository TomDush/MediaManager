package fr.dush.mediamanager.dao.media;

import fr.dush.mediamanager.dao.IDao;
import fr.dush.mediamanager.dao.media.queries.Order;
import fr.dush.mediamanager.dao.media.queries.PaginatedList;
import fr.dush.mediamanager.dao.media.queries.SearchForm;
import fr.dush.mediamanager.dao.media.queries.SearchLimit;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.video.Movie;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Interface to movies saved in database
 *
 * @author Thomas Duchatelle
 */
public interface IMovieDAO extends IDao<Movie, ObjectId> {

    /**
     * Save or update movie, based on {@link Movie#getMediaIds()}.
     */
    void saveOrUpdateMovie(Movie movie);

    /**
     * Increment {@link Movie#getSeen()} by inc.
     *
     * @param inc Number of view to add
     */
    void incrementViewCount(ObjectId id, int inc);

    /**
     * Find movies by IDs provided by sources (imdb, movieDb, ...)
     */
    List<Movie> findBySourceId(SourceId... sourceIds);

    /** Search movies by multiple criteria */
    PaginatedList<Movie> search(SearchForm form, SearchLimit limit, Order... order);

    /**
     * Movies never seen yet.
     *
     * @return Movies ordered by creation in database
     */
    List<Movie> findUnseen();

    /**
     * Find by approximate name
     *
     * @return Movies matching name
     */
    List<Movie> findByTitle(String name);

    /**
     * Find movies by genre.
     *
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

    /** Mark movie as viewed (seen at least as 1) */
    void markAsViewed(ObjectId objectId);

    /** Reset view counter */
    void markAsUnViewed(ObjectId id);
}