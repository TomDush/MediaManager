package fr.dush.mediamanager.business.scanner.impl;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.video.Movie;
import fr.dush.mediamanager.dto.media.video.VideoFile;
import fr.dush.mediamanager.dto.scan.MoviesParsedName;
import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.AmbiguousEnrichment;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.exceptions.ScanException;
import fr.dush.mediamanager.modulesapi.enrich.EnrichException;
import fr.dush.mediamanager.modulesapi.enrich.IMoviesEnricher;

public class MoviesScanner extends AbstractScanner<MoviesParsedName, Movie> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoviesScanner.class);

	@Inject
	private IModulesManager modulesManager;

	@Inject
	private IMovieDAO movieDAO;

	@Inject
	@Configuration(definition = "configuration/scanners.json", packageName = "fr.dush.mediamanager.business.scanner")
	private ModuleConfiguration moduleConfiguration;

	/** Media enricher */
	private IMoviesEnricher enricher;

	@Override
	public ScanStatus startScanning(RootDirectory rootDirectory) throws ScanException {
		String enricherName = rootDirectory.getEnricher();
		if (isBlank(enricherName)) {
			enricherName = moduleConfiguration.readValue("movies.defaultenricher");
		}

		try {
			// Initialize enricher...
			enricher = modulesManager.findModuleById(IMoviesEnricher.class, enricherName);

			return super.startScanning(rootDirectory);

		} catch (ModuleLoadingException e) {
			final String mess = String.format("Can't scan %s : enricher module '%s' not found.", rootDirectory.getPaths(), enricherName);
			throw new ScanException(mess, e);
		}
	}

	@Override
	protected Collection<? extends MoviesParsedName> scanDirectory(Path root) {
		final HashSet<MoviesParsedName> movies = new HashSet<MoviesParsedName>();
		appendScanningFile(root.toFile(), movies);

		return movies;
	}

	@Override
	protected Movie enrich(MoviesParsedName file) {
		LOGGER.debug("Enrich file : {}", file);
		try {
			final List<Movie> movies = enricher.findMediaData(file);

			if (1 != movies.size()) {
				// If enrichment is ambiguous, first one will be choose, but an event is fired to save this confusion, or display it.
				final AmbiguousEnrichment event = new AmbiguousEnrichment(this, Movie.class, file.getFile(), movies);
				ambiguousEnrichmentDispatcher.fire(event);
			}

			// Return first or null
			Movie choosenMovie = null;
			if (movies.isEmpty()) {
				LOGGER.info("No movies found for file {}", file);
				choosenMovie = newEmptyMovie(file);

			} else {
				choosenMovie = movies.get(0); // FIXME Check do no download all jackets
			}

			// Add video informations
			choosenMovie.getVideoFiles().add(file.getVideoFile());

			// Finish to get meta data
			if(!movies.isEmpty()) {
				enricher.enrichMedia(choosenMovie);
			}

			return choosenMovie;

		} catch (EnrichException e) {
			LOGGER.error("Enrichement fail on file {} (movie : {}).", file, file.getMovieName(), e);
		}

		return null;
	}

	@Override
	protected void save(Movie media) {
		movieDAO.save(media);
		// TODO Save collection ?
	}

	/**
	 * Set title and date with file name.
	 *
	 * @param file
	 * @return
	 */
	private Movie newEmptyMovie(MoviesParsedName file) {
		Movie m = new Movie();

		if (isNotEmpty(file.getMovieName())) {
			m.setTitle(file.getMovieName());
		} else {
			m.setTitle(file.getFile().getFileName().toString());
		}

		try {
			if (file.getYear() != 0) {
				DateFormat yearFormatter = new SimpleDateFormat("yyyy");
				m.setRelease(yearFormatter.parse(String.valueOf(file.getYear())));
			}
		} catch (ParseException e) {
			LOGGER.warn("Can't parse date with year = {}", file.getYear());
		}

		// Internal ID based on title (from file name)
		m.getMediaIds().add(new SourceId(SourceId.INTERNAL, Hashing.sha1().hashString(m.getTitle()).toString()));

		return m;
	}

	protected void appendScanningFile(File root, Set<MoviesParsedName> movies) {
		LOGGER.debug("--> scanDirectory({})", root);

		final SetMultimap<String, FileStacking> moviesStacking = HashMultimap.create();

		// Scan all children and parse file names.
		for (File f : getChildren(root)) {
			if (f.isFile()) {
				MoviesParsedName filename = parseFile(f, moviesStacking);
				if (null != filename) {
					movies.add(filename);
				}

			} else if (f.isDirectory()) {
				appendScanningFile(f, movies);
			}

		}

		// Group file part
		for (Entry<String, Collection<FileStacking>> entry : moviesStacking.asMap().entrySet()) {
			if (entry.getValue().size() > 1) {
				LOGGER.debug("{} in {} volumes : {}", entry.getValue().iterator().next().getName(), entry.getValue().size(),
						entry.getValue());

				// Sort multi-file
				final ArrayList<FileStacking> volumeList = newArrayList(entry.getValue());
				Collections.sort(volumeList);

				// Correct parsed file name : remove volume information if necessary
				final FileStacking fileStacking = volumeList.get(0);
				if (fileStacking.getParsedFileName().getMovieName().length() > fileStacking.getName().length()) {
					fileStacking.getParsedFileName().setMovieName(removeSpaces(fileStacking.getName()));
				}

				// Complete good VideoFile and remove duplicate MoviesParsedName
				for (int i = 1; i < volumeList.size(); i++) {
					final FileStacking s = volumeList.get(i);

					fileStacking.getParsedFileName().getVideoFile().getNextParts().add(s.getParsedFileName().getVideoFile().getFile());

					movies.remove(s.getParsedFileName());
				}
			}
		}

		LOGGER.debug("<-- scanDirectory({})", root);
	}

	private MoviesParsedName parseFile(File f, SetMultimap<String, FileStacking> moviesStacking) {
		final String ext = Files.getFileExtension(f.getName());
		if (!scannerConfiguration.getVideoExtensions().contains("." + ext)) return null;

		MoviesParsedName parsedFileName = new MoviesParsedName(f.toPath());

		// Extension
		parsedFileName.setExtension(ext);

		// Name : cleaned and with space
		String simpleName = Files.getNameWithoutExtension(f.getName()).toLowerCase();
		for (String regex : scannerConfiguration.getCleanStrings()) {
			String[] splitted = simpleName.split(regex, 2);
			if (splitted.length > 1) {
				simpleName = splitted[0];
			}
		}
		parsedFileName.setMovieName(removeSpaces(simpleName));

		// Date
		if (null != datePattern) {
			final Matcher matcher = datePattern.matcher(Files.getNameWithoutExtension(f.getName()).toLowerCase());
			if (matcher.matches()) {
				final String year = matcher.group(2);
				parsedFileName.setYear(Integer.parseInt(year));
			}
		}

		// Volume
		for (Pattern pattern : moviesStackingPatterns) {
			final Matcher m = pattern.matcher(f.getName().toLowerCase());
			while (m.find()) {
				final FileStacking stacking = new FileStacking(parsedFileName, m.group(1), m.group(1) + m.group(3) + m.group(4), m.group(2));

				moviesStacking.put(stacking.getFullName(), stacking);
			}
		}

		// Video
		parsedFileName.setVideoFile(new VideoFile(f.toPath()));

		return parsedFileName;
	}

	protected String removeSpaces(String name) {
		return name.replaceAll("[ _\\.\\(\\)\\[\\]\\-]", " ").trim();
	}

	@ToString
	@Getter
	@Setter
	@AllArgsConstructor
	public class FileStacking implements Comparable<FileStacking> {

		private MoviesParsedName parsedFileName;

		/** Name, before volume information */
		private String name;

		/** Full name without volume information, used as key to match with other volumes */
		private String fullName;

		/** Volume information */
		private String volume;

		@Override
		public int compareTo(FileStacking o) {
			return volume.compareTo(o.getVolume());
		}
	}
}
