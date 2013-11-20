package fr.dush.mediamanager.business.scanner.impl;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.configuration.producers.ScannerConfigurationProducer;
import fr.dush.mediamanager.business.mediatech.IRootDirectoryManager;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.domain.configuration.ScannerConfiguration;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.domain.scan.MoviesParsedName;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.domain.tree.MediaType;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.events.scan.AmbiguousEnrichment;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;
import fr.dush.mediamanager.modulesapi.enrich.IMoviesEnricher;

public class MoviesScannerTest extends SimpleJunitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoviesScannerTest.class);

	@InjectMocks
	private MoviesScanner scanner;

	@Spy
	protected ScannerConfiguration scannerConfiguration = ScannerConfigurationProducer.SCANNER_CONFIGURATION;

	@Mock
	private IModulesManager modulesManager;

	@Mock
	private IMoviesEnricher enricher;

	@Mock
	protected Event<AmbiguousEnrichment> ambiguousEnrichmentDispatcher;

	@Mock
	private ModuleConfiguration moduleConfiguration;

	@Mock
	private IMovieDAO movieDAO;

	@Mock
	private Instance<ScanningExceptionHandler> scanningExceptionHandlerFactory;

	@Mock
	private IRootDirectoryManager rootDirectoryManager;

	private Throwable exception = null;

	@Spy
	private ScanningExceptionHandler handler = new ScanningExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			LOGGER.error("An error was throws", e);
			exception = e;
			fail(e.getMessage(), e);
		}

	};

	@After
	public void throwErrorIfAny() throws Exception {
		if (exception != null) {
			Throwable e = exception;
			exception = null;
			fail(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Before
	public void postConstruct() throws ModuleLoadingException {
		when(modulesManager.findModuleById(any(Class.class), anyString())).thenReturn(enricher);
		when(scanningExceptionHandlerFactory.get()).thenReturn(handler);
		scanner.initializePatterns();

		when(moduleConfiguration.readValue(anyString())).thenReturn("default-enricher");
	}

	@Test
	public void testScanning() throws Exception {
		create_files();

		final RootDirectory rootDirectory = new RootDirectory();
		rootDirectory.setMediaType(MediaType.MOVIE);
		rootDirectory.setName("Movies Junit Database");
		rootDirectory.getPaths().add("target/movies");
		rootDirectory.setEnricher("");

		// Exec
		final ScanStatus status = scanner.startScanning(rootDirectory);

		while (status.isInProgress()) {
			LOGGER.info("Scanning in progress : {}", status);
			Thread.sleep(30);
		}

		// Test
		verify(modulesManager).findModuleById(IMoviesEnricher.class, "default-enricher");

		verify(enricher).findMediaData(argParsedName("2012", 0));
		verify(enricher).findMediaData(argParsedName("angels and demons", 0));
		verify(enricher).findMediaData(argParsedName("battle los angeles", 2011));
		verify(enricher).findMediaData(argParsedName("5 days of war", 2011));
		verify(enricher).findMediaData(argParsedName("the postman", 1998));
		verify(movieDAO, times(5)).saveOrUpdateMovie(any(Movie.class));

	}

	private static MoviesParsedName argParsedName(final String movieName, final int year) {
		return argThat(new BaseMatcher<MoviesParsedName>() {

			private String error = "";

			@Override
			public boolean matches(Object item) {
				if (item instanceof MoviesParsedName) {
					MoviesParsedName name = (MoviesParsedName) item;
					if (!movieName.equals(name.getMovieName()) || year != name.getYear()) {
						error = String.format("expected name equals <%s>, but was <%s> ; and date <%d>, but was <%d>", movieName, name.getMovieName(),
								year, name.getYear());
						return false;
					}

					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("MoviesParsedName doesn't match : " + error);

			}
		});
	}

	private void create_files() throws Exception {
		File root = new File("target/movies");

		if (root.exists()) root.delete();
		root.mkdirs();
		new File(root, "war").mkdir();

		new File(root, "2012-1080p.mkv").createNewFile();
		new File(root, "Angels.And.Demons.FRENCH.DVDRIP.VERSION.NTSC.REPACK.1CD.XviD.kobra.avi").createNewFile();
		new File(root, "Angels.And.Demons.FRENCH.DVDRIP.VERSION.NTSC.REPACK.1CD.XviD.kobra.sub").createNewFile();
		new File(root, "The Postman (1998) DVD-Rip French BivX (FR-ENG) Titanic Team part1.avi").createNewFile();
		new File(root, "The Postman (1998) DVD-Rip French BivX (FR-ENG) Titanic Team part2.avi").createNewFile();
		new File(root, "war/5.Days.Of.War.2011.FRENCH.SUBFORCED.BRRiP.XviD-AUTOPSiE.avi").createNewFile();
		new File(root, "war/Battle.Los.Angeles.2011.TRUEFRENCH.BRRiP.XviD.AC3-AUTOPSiE.avi").createNewFile();

		// new File(root, "300.avi").createNewFile();
		// new File(root, "Angle.D.Attaque.Subforced.Truefrench.Dvdrip.Xvid.AC3.CD1-FwD.avi").createNewFile();
		// new File(root, "Angle.D.Attaque.Subforced.Truefrench.Dvdrip.Xvid.AC3.CD2-FwD.avi").createNewFile();
		// new File(root, "Cloverfield.FRENCH.DVDRiP.XviD-iD.avi").createNewFile();
		// new File(root, "Fast and furious 4.avi").createNewFile();
		// new File(root, "Transformers Revenge Of The Fallen Truefrench Dvdrip Xvid-RLD.CD01.avi").createNewFile();
		// new File(root, "Transformers Revenge Of The Fallen Truefrench Dvdrip Xvid-RLD.CD02.avi").createNewFile();

	}

	@Test
	public void testDateParser() throws Exception {
		String filmName = "Sherlock.Holmes.2009.CD1";

		final Pattern pattern = Pattern.compile(ScannerConfigurationProducer.SCANNER_CONFIGURATION.getDateRegex());
		final Matcher m = pattern.matcher(filmName);

		assertThat(m.matches()).isTrue();
		assertThat(m.group(2)).isEqualTo("2009");
	}
}
