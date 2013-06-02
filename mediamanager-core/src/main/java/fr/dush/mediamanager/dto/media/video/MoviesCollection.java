package fr.dush.mediamanager.dto.media.video;

import static com.google.common.collect.Lists.*;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.dto.media.Media;

/**
 * Collection of movies.
 *
 * <p>
 * <code>MovieFilmsCollection</code>s and {@link Movie}s are {@link Media}s, thus there are persisted on same place. <code>FilmsCollection</code>
 * s are saved 2 times :
 * <ul>
 * <li>first directly in media/movie column</li>
 * <li>second in each movie of the collection</li>
 * </ul>
 * </p>
 *
 * <p>
 * But films are only saved once. {@link #availableMovies} is <b>lazy</b> attribute.
 * </p>
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class MoviesCollection extends Media {

	/** Total part in this collection */
	private int totalPart;

	/** Backdrops image (if any) */
	private String backdrop;

	/** List of movies in this collection. This list is LAZY and is not saved in database. */
	private List<Movie> availableMovies = newArrayList();

	/** Full list of collection's movie, only ID are saved. Only principal data are saved in this list. DO NOT USE TO LAUNCH MOVIES ! */
	private List<Movie> movies = newArrayList();

}
