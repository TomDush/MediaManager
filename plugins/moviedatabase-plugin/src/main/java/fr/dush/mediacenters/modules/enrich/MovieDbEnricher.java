package fr.dush.mediacenters.modules.enrich;

import static com.google.common.collect.Collections2.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.Trailer;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.Sources;
import fr.dush.mediamanager.dto.media.video.Film;
import fr.dush.mediamanager.modulesapi.enrich.EnrichException;
import fr.dush.mediamanager.modulesapi.enrich.IEnrichFilm;
import fr.dush.mediamanager.modulesapi.enrich.ParsedFileName;
import fr.dush.mediamanager.modulesapi.enrich.TrailerLink;

/**
 * Find meta-data on films and shows from <i>dbmovies</i>.
 *
 * @author Thomas Duchatelle
 *
 */
@Module(name = "MoviesDB Plugin", description = "Find data on films and shows with http://www.themoviedb.org/")
public class MovieDbEnricher implements IEnrichFilm {

	public static final String MOVIEDB_ID_TYPE = "moviedb";

	private static final Logger LOGGER = LoggerFactory.getLogger(MovieDbEnricher.class);

	private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	@Inject
	private TheMovieDbApi api;

	@Inject
	private IArtDownloader metaMediaManager;

	@Override
	public List<Film> findMediaData(ParsedFileName filename) throws EnrichException {

		try {
			// Search in database
			final List<MovieDb> foundMovies = api.searchMovie(filename.getMovieName(), filename.getYear(), "en", false, 0);

			// Convert informations
			return Lists.transform(foundMovies, converter);

		} catch (MovieDbException e) {
			throw new EnrichException(String.format("An error occured when searching film %s on TheMovieDb : %s", filename.getMovieName(),
					e.getMessage()), e);
		}

	}

	@Override
	public void enrichMedia(Media media) throws EnrichException {
		// Get movieDB id, exception if not.
		final int id = getId(media);

		// Enrich Film data
		if (media instanceof Film) {
			Film film = (Film) media;

			enrichFilm(id, film);

		}

	}

	@Override
	public List<TrailerLink> getTrailers(Media media, String lang) throws EnrichException {
		try {
			return Lists.transform(api.getMovieTrailers(getId(media), isBlank(lang) ? "en" : lang), trailerConverter);
		} catch (MovieDbException e) {
			throw new EnrichException(String.format("Can't find trailers for media {}.", media), e);
		}

	}

	/**
	 * Get and cast MovieDB ID.
	 *
	 * @param media Media which must have MovieDB id.
	 * @return MovieDB id.
	 * @throws EnrichException
	 */
	private int getId(Media media) throws EnrichException {
		final Collection<String> ids = media.getMediaIds().getIds(MOVIEDB_ID_TYPE);
		if (ids.isEmpty()) {
			throw new EnrichException(String.format("No id(s) provided for %s enricher for film %s. Can not process to enrichment.",
					MOVIEDB_ID_TYPE, media.getTitle()));
		}

		final int id = Integer.parseInt(ids.iterator().next());
		return id;
	}

	private void enrichFilm(final int id, Film film) throws EnrichException {
		try {
			// Find Genres, overview, ...
			final MovieDb movieDb = api.getMovieInfo(id, "en");

			if (movieDb.getGenres() != null) {
				film.getGenres().addAll(transform(movieDb.getGenres(), new Function<Genre, String>() {

					@Override
					public String apply(Genre genre) {
						return genre.getName();
					}
				}));
			}
			film.setOverview(movieDb.getOverview());
			film.getBackdrops().add(
					metaMediaManager.storeImage(api.createImageUrl(movieDb.getBackdropPath(), "original"), movieDb.getTitle()));

			// Find main actors...
			PersonParser parser = new PersonParser(this, api.getMovieCasts(id));
			film.setMainActors(parser.getCasting(5));
			film.setDirectors(parser.getDirectors());

			// TODO Treat film collections
			final com.omertron.themoviedbapi.model.Collection collection = movieDb.getBelongsToCollection();
			if (null != collection) {
				film.setTitle(film.getTitle() + " (collection : " + collection.getTitle() + ")");
			}

		} catch (MovieDbException e) {
			throw new EnrichException(String.format("Can't enrich %s film : %s", film.getTitle(), e.getMessage()));
		}
	}

	private Function<MovieDb, Film> converter = new Function<MovieDb, Film>() {

		@Override
		public Film apply(MovieDb movieDb) {

			Film film = new Film();

			// Ids
			film.getMediaIds().addId(new SourceId(MOVIEDB_ID_TYPE, Integer.toString(movieDb.getId())));
			if (isNotBlank(movieDb.getImdbID())) {
				film.getMediaIds().addId(new SourceId(Sources.IMDB, movieDb.getImdbID()));
			}

			// General informations
			film.setCreation(new Date());
			film.setTitle(movieDb.getTitle());
			if (isNotBlank(movieDb.getReleaseDate())) {
				try {
					film.setRelease(dateFormatter.parse(movieDb.getReleaseDate()));
				} catch (ParseException e) {
					LOGGER.warn("Can't parse date {} : {}", movieDb.getReleaseDate(), e.getMessage());
				}
			}

			// TODO (gérer les collections)

			// Posters, ....
			try {
				film.setPoster(metaMediaManager.storeImage(api.createImageUrl(movieDb.getPosterPath(), "original"), movieDb.getTitle()));
			} catch (MovieDbException e) {
				LOGGER.warn("Can't download file {} : {}", movieDb.getPosterPath(), e.getMessage(), e);
			}

			return film;
		}
	};

	private Function<Trailer, TrailerLink> trailerConverter = new Function<Trailer, TrailerLink>() {

		@Override
		public TrailerLink apply(Trailer movieDb) {
			final TrailerLink link = new TrailerLink();
			link.setName(movieDb.getName());
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

	public TheMovieDbApi getApi() {
		return api;
	}

	public IArtDownloader getMetaMediaManager() {
		return metaMediaManager;
	}
}
