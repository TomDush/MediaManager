package fr.dush.mediamanager.business.mediatech.scanner;

import static com.google.common.collect.Lists.*;

import java.io.File;
import java.nio.file.Path;
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
import com.google.common.io.Files;

import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.media.video.Movie;
import fr.dush.mediamanager.dto.media.video.VideoFile;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.AmbiguousEnrichment;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;
import fr.dush.mediamanager.exceptions.ScanningException;
import fr.dush.mediamanager.modulesapi.enrich.EnrichException;
import fr.dush.mediamanager.modulesapi.enrich.IMoviesEnricher;

public class MoviesScanner extends AbstractScanner<MoviesParsedName> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoviesScanner.class);

	@Inject
	private IModulesManager modulesManager;

	/** Media enricher */
	private IMoviesEnricher enricher;

	@Override
	public ScanningStatus startScanning(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException, ScanningException {
		try {
			// Initialize enricher...
			enricher = modulesManager.findModuleById(IMoviesEnricher.class, rootDirectory.getEnricherScanner());
			return super.startScanning(rootDirectory);

		} catch (ModuleLoadingException e) {
			final String mess = String.format("Can't scan %s : enricher module '%s' not found.", rootDirectory.getPaths(),
					rootDirectory.getEnricherScanner());
			throw new ScanningException(mess, e);
		}
	}

	@Override
	protected Collection<? extends MoviesParsedName> scanDirectory(Path root) {
		final HashSet<MoviesParsedName> movies = new HashSet<MoviesParsedName>();
		appendScanningFile(root.toFile(), movies);

		return movies;
	}

	@Override
	protected Media enrich(MoviesParsedName file) {
		LOGGER.debug("Enrich file : {}", file);
		try {
			final List<Movie> movies = enricher.findMediaData(file);

			if (1 != movies.size()) {
				// If enrichment is ambiguous, first one will be choose, but an event is fired to save this confusion, or display it.
				final AmbiguousEnrichment event = new AmbiguousEnrichment(this, Movie.class, file.getFile(), movies);
				ambiguousEnrichmentDispatcher.fire(event);
			}

			// Return first or null
			if (movies.isEmpty()) return null;
			return movies.get(0);

		} catch (EnrichException e) {
			LOGGER.error("Enrichement fail on '{}' movie : {}", file.getMovieName(), e.getMessage());
		}

		return null;
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
