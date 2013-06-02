package fr.dush.mediacenters.modules.enrich;

import static com.google.common.collect.Collections2.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.CollectionInfo;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.Trailer;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.business.mediatech.scanner.MoviesParsedName;
import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.Sources;
import fr.dush.mediamanager.dto.media.video.BelongToCollection;
import fr.dush.mediamanager.dto.media.video.Movie;
import fr.dush.mediamanager.dto.media.video.MoviesCollection;
import fr.dush.mediamanager.dto.media.video.Trailers;
import fr.dush.mediamanager.modulesapi.enrich.EnrichException;
import fr.dush.mediamanager.modulesapi.enrich.FindTrailersEvent;
import fr.dush.mediamanager.modulesapi.enrich.IMoviesEnricher;

/**
 * Find meta-data on movies and shows from <i>themoviesdb</i>.
 *
 * @author Thomas Duchatelle
 *
 */
@Module(name = "MoviesDB Plugin", description = "Find data on movies and shows with http://www.themoviedb.org/", id="enricher-themoviesdb")
public class TheMovieDbEnricher implements IMoviesEnricher {

	public static final String MOVIEDB_ID_TYPE = "moviedb";

	private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbEnricher.class);

	private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	@Inject
	private TheMovieDbApi api;

	@Inject
	private IArtDownloader downloader;

	@Override
	public List<Movie> findMediaData(MoviesParsedName filename) throws EnrichException {

		try {
			// Search in database
			final List<MovieDb> foundMovies = api.searchMovie(filename.getMovieName(), filename.getYear(), "en", false, 0);

			// Convert informations
			return Lists.transform(foundMovies, converter);

		} catch (MovieDbException e) {
			throw new EnrichException(String.format("An error occured when searching movie %s on TheMovieDb : %s", filename.getMovieName(),
					e.getMessage()), e);
		}

	}

	@Override
	public void enrichMedia(Media media) throws EnrichException {
		// Get movieDB id, exception if not.
		final int id = getId(media.getMediaIds(), media.getTitle());

		// Enrich Movie data
		if (media instanceof Movie) {
			Movie movie = (Movie) media;

			enrichMovie(id, movie);

		}

	}

	@Override
	public List<fr.dush.mediamanager.dto.media.video.Trailer> findTrailers(Media media, String lang) throws EnrichException {
		try {
			final int id = getId(media.getMediaIds(), media.getTitle());
			return Lists.transform(api.getMovieTrailers(id, isBlank(lang) ? "en" : lang), trailerConverter);
		} catch (MovieDbException e) {
			throw new EnrichException(String.format("Can't find trailers for media {}.", media), e);
		}

	}

	/**
	 * Observe and complete {@link Movie} medias when listen {@link FindTrailersEvent}.
	 *
	 * @param event
	 */
	public void completeTrailers(@Observes FindTrailersEvent event) {

		try {
			// If it's movie and MoviesDB's id is known, research available trailers.
			if (event.getMedia() instanceof Movie) {
				Movie movie = (Movie) event.getMedia();
				final List<fr.dush.mediamanager.dto.media.video.Trailer> trailers = findTrailers(movie, event.getLang());

				if(movie.getTrailers() == null) {
					movie.setTrailers(new Trailers());
				}
				movie.getTrailers().getSources().add(MOVIEDB_ID_TYPE);
				movie.getTrailers().setRefreshed(new Date());
				for (fr.dush.mediamanager.dto.media.video.Trailer t : trailers) {
					movie.getTrailers().addTrailer(t);
				}
			}
		} catch (EnrichException e) {
			LOGGER.info("Can find trailers for {} : {}", event.getMedia().getTitle(), e.getMessage());
		}

	}

	@Override
	public MoviesCollection findCollection(BelongToCollection belongToCollection) throws EnrichException {
		try {
			final CollectionInfo info = api.getCollectionInfo(getId(belongToCollection.getMediaIds(), belongToCollection.getTitle()), "en");

			// General collection's data
			MoviesCollection collection = new MoviesCollection();
			collection.getMediaIds().addId(createId(info.getId()));
			collection.setCreation(new Date());
			collection.setPoster(downloadImage(info.getPosterPath(), collection.getTitle() + "_poster"));
			collection.setBackdrop(downloadImage(info.getBackdropPath(), collection.getTitle() + "_backdrop"));

			// List of movies
			collection.setMovies(Lists.transform(info.getParts(), movieCollectionConverter));

			return collection;

		} catch (MovieDbException e) {
			throw new EnrichException(String.format("Can't find data on collection %s (ids : %s)", belongToCollection.getTitle(),
					belongToCollection.getMediaIds()));
		}
	}

	public String downloadImage(String posterPath, String baseName) throws MovieDbException {
		return downloader.storeImage(api.createImageUrl(posterPath, "original"), baseName);
	}

	/**
	 * Get and cast MovieDB ID.
	 *
	 * @param media Media which must have MovieDB id.
	 * @return MovieDB id.
	 * @throws EnrichException
	 */
	private static int getId(Sources sourceIds, String title) throws EnrichException {
		final Collection<String> ids = sourceIds.getIds(MOVIEDB_ID_TYPE);
		if (ids.isEmpty()) {
			throw new EnrichException(String.format("No id(s) provided for %s enricher for film %s. Can not process to enrichment.",
					MOVIEDB_ID_TYPE, title));
		}

		final int id = Integer.parseInt(ids.iterator().next());
		return id;
	}

	private void enrichMovie(final int id, Movie movie) throws EnrichException {
		try {
			// Find Genres, overview, ...
			final MovieDb movieDb = api.getMovieInfo(id, "en");

			if (movieDb.getGenres() != null) {
				movie.getGenres().addAll(transform(movieDb.getGenres(), new Function<Genre, String>() {

					@Override
					public String apply(Genre genre) {
						return genre.getName();
					}
				}));
			}
			movie.setOverview(movieDb.getOverview());
			movie.getBackdrops().add(downloadImage(movieDb.getBackdropPath(), movieDb.getTitle()));

			// Find main actors...
			PersonParser parser = new PersonParser(this, api.getMovieCasts(id));
			movie.setMainActors(parser.getCasting(5));
			movie.setDirectors(parser.getDirectors());

			// Treat collection data
			final com.omertron.themoviedbapi.model.Collection collection = movieDb.getBelongsToCollection();
			if (null != collection) {
				BelongToCollection movieCollection = movie.getCollection();
				if (null == movieCollection) movieCollection = new BelongToCollection();
				movieCollection.getMediaIds().addId(createId(collection.getId()));
				movieCollection.setTitle(collection.getTitle());

				movie.setCollection(movieCollection);
			}

		} catch (MovieDbException e) {
			throw new EnrichException(String.format("Can't enrich %s film : %s", movie.getTitle(), e.getMessage()));
		}
	}

	private Function<MovieDb, Movie> converter = new Function<MovieDb, Movie>() {

		@Override
		public Movie apply(MovieDb movieDb) {

			Movie movie = new Movie();

			// Ids
			movie.getMediaIds().addId(createId(movieDb.getId()));
			if (isNotBlank(movieDb.getImdbID())) {
				movie.getMediaIds().addId(new SourceId(Sources.IMDB, movieDb.getImdbID()));
			}

			// General informations
			movie.setCreation(new Date());
			movie.setTitle(movieDb.getTitle());
			if (isNotBlank(movieDb.getReleaseDate())) {
				try {
					movie.setRelease(dateFormatter.parse(movieDb.getReleaseDate()));
				} catch (ParseException e) {
					LOGGER.warn("Can't parse date {} : {}", movieDb.getReleaseDate(), e.getMessage());
				}
			}

			// Posters, ....
			try {
				movie.setPoster(downloadImage(movieDb.getPosterPath(), movieDb.getTitle()));
			} catch (MovieDbException e) {
				LOGGER.warn("Can't download file {} : {}", movieDb.getPosterPath(), e.getMessage(), e);
			}

			return movie;
		}
	};

	private Function<com.omertron.themoviedbapi.model.Collection, Movie> movieCollectionConverter = new Function<com.omertron.themoviedbapi.model.Collection, Movie>() {

		@Override
		public Movie apply(com.omertron.themoviedbapi.model.Collection collection) {

			Movie movie = new Movie();

			// Ids
			movie.getMediaIds().addId(createId(collection.getId()));

			// General informations
			movie.setCreation(new Date());
			movie.setTitle(collection.getTitle());
			if (isNotBlank(collection.getReleaseDate())) {
				try {
					movie.setRelease(dateFormatter.parse(collection.getReleaseDate()));
				} catch (ParseException e) {
					LOGGER.warn("Can't parse date {} : {}", collection.getReleaseDate(), e.getMessage());
				}
			}

			// Posters, ....
			try {
				movie.setPoster(downloadImage(collection.getPosterPath(), collection.getTitle()));
			} catch (MovieDbException e) {
				LOGGER.warn("Can't download file {} : {}", collection.getPosterPath(), e.getMessage(), e);
			}

			return movie;
		}
	};

	/**
	 * Create MediaManager ID from MoviesDB ID.
	 *
	 * @param id
	 * @return
	 */
	protected static SourceId createId(final int id) {
		return new SourceId(MOVIEDB_ID_TYPE, Integer.toString(id));
	}

	private Function<Trailer, fr.dush.mediamanager.dto.media.video.Trailer> trailerConverter = new Function<Trailer, fr.dush.mediamanager.dto.media.video.Trailer>() {

		@Override
		public fr.dush.mediamanager.dto.media.video.Trailer apply(Trailer movieDb) {
			final fr.dush.mediamanager.dto.media.video.Trailer link = new fr.dush.mediamanager.dto.media.video.Trailer();
			link.setTitle(movieDb.getName());
			link.setQuality(movieDb.getSize());
			link.setSource(movieDb.getWebsite());

			if ("youtube".equals(movieDb.getWebsite().toLowerCase())) {
				// Generate URL for YOUTUBE
				link.setUrl("http://www.youtube.com/watch?v=" + movieDb.getSource());

			} else {
				// Website is unknown...
				LOGGER.warn("Website {} is unkown, check configuration.");
				link.setUrl(String.format("Can't generate URL for website %s (identifier id %s)", movieDb.getWebsite(), movieDb.getSource()));
			}

			return link;
		}
	};

}
