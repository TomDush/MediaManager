package fr.dush.mediamanager.business.mediatech.scanner.impl;

import static com.google.common.collect.Lists.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.event.Event;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.configuration.producers.ScannerConfigurationProducer;
import fr.dush.mediamanager.business.mediatech.scanner.MoviesParsedName;
import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
import fr.dush.mediamanager.business.mediatech.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.dto.configuration.ScannerConfiguration;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.events.scan.reponses.AmbiguousEnrichment;
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

	@SuppressWarnings("unchecked")
	@Before
	public void postConstruct() throws ModuleLoadingException {
		when(modulesManager.findModuleById(any(Class.class), anyString())).thenReturn(enricher);
		scanner.initializePatterns();
	}

	@Test
	public void testScanning() throws Exception {
		create_files();

		final RootDirectory rootDirectory = new RootDirectory();
		rootDirectory.setName("Movies Junit Database");
		rootDirectory.getPaths().add("target/movies");
		rootDirectory.setEnricherScanner("my-junit-enricher");

		// Exec
		final ScanningStatus status = scanner.startScanning(rootDirectory);

		while (status.isInProgress()) {
			LOGGER.info("Scanning in progress : {}", status);
			Thread.sleep(1000);
		}

		// Test
		verify(modulesManager).findModuleById(IMoviesEnricher.class, "my-junit-enricher");

		verify(enricher).findMediaData(argParsedName("2012", 0));
		verify(enricher).findMediaData(argParsedName("angels and demons", 0));
		verify(enricher).findMediaData(argParsedName("battle los angeles", 2011));
		verify(enricher).findMediaData(argParsedName("5 days of war", 2011));
		verify(enricher).findMediaData(argParsedName("the postman", 1998));
//		verify(enricher).findMediaData(argParsedName("the postman", 1998));

	}

	private static MoviesParsedName argParsedName(final String movieName, final int year) {
		return argThat(new BaseMatcher<MoviesParsedName>() {

			private String error = "";

			@Override
			public boolean matches(Object item) {
				if (item instanceof MoviesParsedName) {
					MoviesParsedName name = (MoviesParsedName) item;
					if (!movieName.equals(name.getMovieName()) || year != name.getYear()) {
						error = String.format("expected name equals <%s>, but was <%s> ; and date <%d>, but was <%d>", movieName,
								name.getMovieName(), year, name.getYear());
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

	// @Before
	public void create_files() throws Exception {
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

	// @Test
	public void list_files() throws Exception {
		final List<File> files = newArrayList(Paths.get("/mnt/data/Films/Films vf/").toFile().listFiles());
		Collections.sort(files);
		for (File f : files) {
			System.out.println("new File(root, \"" + f.getName() + "\").createNewFile();");
		}
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
