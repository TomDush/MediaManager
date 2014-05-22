package fr.dush.mediamanager.business.scanner.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.media.video.Person;
import fr.dush.mediamanager.domain.media.video.VideoFile;
import fr.dush.mediamanager.domain.scan.MoviesParsedName;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.AmbiguousEnrichment;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.exceptions.ScanException;
import fr.dush.mediamanager.modulesapi.enrich.EnrichException;
import fr.dush.mediamanager.modulesapi.enrich.IMoviesEnricher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Sets.*;
import static org.apache.commons.lang3.StringUtils.*;

public class MoviesScanner extends AbstractScanner<MoviesParsedName, Movie> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesScanner.class);

    @Inject
    private IModulesManager modulesManager;

    @Inject
    private IMovieDAO movieDAO;

    @Inject
    @Config(id = "modules",
            definition = "configuration/modules.json",
            packageName = "fr.dush.mediamanager.business.scanner")
    private ModuleConfiguration moduleConfiguration;

    /** Media enricher */
    private IMoviesEnricher enricher;

    @Inject
    private EventBus eventBus;

    /** Pattern to find date in filenames */
    protected Pattern datePattern;

    /** Pattern matching movies split into 2 or more files. */
    protected Set<Pattern> moviesStackingPatterns = newHashSet();

    @PostConstruct
    public void initializePatterns() {

        // Pre-compile patterns...
        if (isNotEmpty(getScannerConfiguration().getDateRegex())) {
            datePattern = Pattern.compile(getScannerConfiguration().getDateRegex());
        }

        for (String regex : getScannerConfiguration().getMoviesStacking()) {
            moviesStackingPatterns.add(Pattern.compile(regex));
        }
    }

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
            final String mess = String.format("Can't scan %s : enricher module '%s' not found.",
                                              rootDirectory.getPaths(),
                                              enricherName);
            throw new ScanException(mess, e);
        }
    }

    @Override
    protected Collection<? extends MoviesParsedName> scanDirectory(Path root) {
        final HashSet<MoviesParsedName> movies = new HashSet<>();
        appendScanningFile(root.toFile(), movies);

        return movies;
    }

    @Override
    protected Movie enrich(MoviesParsedName file) {
        LOGGER.debug("Enrich file : {}", file);
        try {
            final List<Movie> movies = enricher.findMediaData(file);

            if (movies.size() != 1) {
                // If enrichment is ambiguous, first one will be choose, but an event is fired to save this
                // confusion, or display it.
                final AmbiguousEnrichment event = new AmbiguousEnrichment(this, Movie.class, file.getFile(), movies);
                eventBus.post(event);
            }

            // Return first or null
            Movie chosenMovie = null;
            if (movies.isEmpty()) {
                LOGGER.info("No movies found for file {}", file);
                chosenMovie = newEmptyMovie(file);
            } else {
                chosenMovie = movies.get(0);

                // Finish to get meta data (if
                enricher.enrichMedia(chosenMovie);

                // Download elements
                if (isNotEmpty(chosenMovie.getPoster())) {
                    addToDownloadList(chosenMovie.getPoster(),
                                      new ArtQuality[]{ArtQuality.MINI, ArtQuality.THUMBS, ArtQuality.DISPLAY});
                    for (Person p : chosenMovie.getMainActors()) {
                        addToDownloadList(p.getPicture(), ArtQuality.MINI);
                    }
                    for (Person p : chosenMovie.getDirectors()) {
                        addToDownloadList(p.getPicture(), ArtQuality.MINI);
                    }

                    if (!chosenMovie.getBackdrops().isEmpty()) {
                        addToDownloadList(chosenMovie.getBackdrops().get(0), ArtQuality.MINI);
                    }
                }
            }

            // Add video information
            chosenMovie.getVideoFiles().add(file.getVideoFile());

            return chosenMovie;

        } catch (EnrichException e) {
            LOGGER.error("Enrichment fail on file {} (movie : {}).", file, file.getMovieName(), e);
        }

        return null;
    }

    private void addToDownloadList(String artRef, ArtQuality... artQualities) {
        if (isNotEmpty(artRef)) {
            getDownloader().append(artRef, artQualities);
        }
    }

    @Override
    protected void save(Movie media) {
        movieDAO.saveOrUpdateMovie(media);
        // TODO Save collection ?
    }

    /**
     * Set title and date with file name.
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
        m.getMediaIds()
         .add(new SourceId(SourceId.INTERNAL,
                           Hashing.sha1().hashString(m.getTitle(), Charset.forName("UTF-8")).toString()));

        return m;
    }

    protected void appendScanningFile(File root, Set<MoviesParsedName> movies) {
        LOGGER.debug("--> scanDirectory({})", root);

        final SetMultimap<String, FileStacking> moviesStacking = HashMultimap.create();

        // Scan all children and parse file names.
        for (File f : getChildren(root)) {
            if (f.isFile()) {
                MoviesParsedName filename = parseFile(f, moviesStacking);
                if (filename != null) {
                    movies.add(filename);
                }
            } else if (f.isDirectory()) {
                appendScanningFile(f, movies);
            }
        }

        // Group file part
        for (Entry<String, Collection<FileStacking>> entry : moviesStacking.asMap().entrySet()) {
            if (entry.getValue().size() > 1) {
                LOGGER.debug("{} in {} volumes : {}",
                             entry.getValue().iterator().next().getName(),
                             entry.getValue().size(),
                             entry.getValue());

                // Sort multi-file
                final ArrayList<FileStacking> volumeList = Lists.newArrayList(entry.getValue());
                Collections.sort(volumeList);

                // Correct parsed file name : remove volume information if necessary
                final FileStacking fileStacking = volumeList.get(0);
                if (fileStacking.getParsedFileName().getMovieName().length() > fileStacking.getName().length()) {
                    fileStacking.getParsedFileName().setMovieName(removeSpaces(fileStacking.getName()));
                }

                // Complete good VideoFile and remove duplicate MoviesParsedName
                for (int i = 1; i < volumeList.size(); i++) {
                    final FileStacking s = volumeList.get(i);

                    fileStacking.getParsedFileName()
                                .getVideoFile()
                                .getNextParts()
                                .add(s.getParsedFileName().getVideoFile().getFile());

                    movies.remove(s.getParsedFileName());
                }
            }
        }

        LOGGER.debug("<-- scanDirectory({})", root);
    }

    private MoviesParsedName parseFile(File f, SetMultimap<String, FileStacking> moviesStacking) {
        final String ext = Files.getFileExtension(f.getName()).toLowerCase();
        if (!getScannerConfiguration().getVideoExtensions().contains("." + ext)) {
            return null;
        }

        MoviesParsedName parsedFileName = new MoviesParsedName(f.toPath());

        // Extension
        parsedFileName.setExtension(ext);

        // Name : cleaned and with space
        String simpleName = Files.getNameWithoutExtension(f.getName()).toLowerCase();
        for (String regex : getScannerConfiguration().getCleanStrings()) {
            String[] splitted = simpleName.split(regex, 2);
            if (splitted.length > 1) {
                simpleName = splitted[0];
            }
        }
        parsedFileName.setMovieName(removeSpaces(simpleName));

        // Date
        if (datePattern != null) {
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
                final FileStacking stacking =
                        new FileStacking(parsedFileName, m.group(1), m.group(1) + m.group(3) + m.group(4), m.group(2));

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
