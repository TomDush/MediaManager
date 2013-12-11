package fr.dush.mediamanager.modulesapi.enrich;

import java.util.List;

import fr.dush.mediamanager.domain.media.Media;
import fr.dush.mediamanager.domain.media.video.BelongToCollection;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.MoviesCollection;
import fr.dush.mediamanager.domain.media.video.Trailer;
import fr.dush.mediamanager.domain.scan.MoviesParsedName;

/**
 * Bean to enrich medias meta-data : find on web informations from file name.
 *
 * @author Thomas Duchatelle
 *
 */
public interface IMoviesEnricher {

	/**
	 * Search movie from parsed file name.
	 *
	 * @param filename
	 * @return
	 * @throws EnrichException If an error occurred, this exception is thrown.
	 */
	List<Movie> findMediaData(MoviesParsedName filename) throws EnrichException;

	/**
	 * Get completed information on media.
	 *
	 * @param media Selected movie
	 * @throws EnrichException
	 */
	void enrichMedia(Media media) throws EnrichException;

	/**
	 * Find data on collection
	 *
	 * @param collection
	 * @return
	 * @throws EnrichException
	 */
	MoviesCollection findCollection(BelongToCollection collection) throws EnrichException;

	/**
	 * List available trailers for this movie.
	 *
	 * @param media
	 * @param lang Expected trailer language.
	 * @return
	 * @throws EnrichException
	 */
	List<Trailer> findTrailers(Media media, String lang) throws EnrichException;
}
