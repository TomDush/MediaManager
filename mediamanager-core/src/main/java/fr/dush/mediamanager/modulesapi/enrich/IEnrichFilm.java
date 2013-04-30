package fr.dush.mediamanager.modulesapi.enrich;

import java.util.List;

import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.media.video.Film;

/**
 * Bean to enrich medias meta-data : find on web informations from file name.
 *
 * @author Thomas Duchatelle
 *
 */
public interface IEnrichFilm {

	/** Module name */
	String getName();

	/** Module description */
	String getDescription();

	/**
	 * Search film from parsed file name.
	 *
	 * @param media
	 * @return
	 * @throws EnrichException If an error occurred, this exception is thrown.
	 */
	List<Film> findMediaData(ParsedFileName filename) throws EnrichException;

	/**
	 * Get completed information on media.
	 *
	 * @param media Selected film
	 * @throws EnrichException
	 */
	void enrichMedia(Media media) throws EnrichException;

	/**
	 * List available trailers for this film.
	 *
	 * @param media
	 * @param lang TODO
	 * @return
	 * @throws EnrichException
	 */
	List<TrailerLink> getTrailers(Media media, String lang) throws EnrichException;
}
