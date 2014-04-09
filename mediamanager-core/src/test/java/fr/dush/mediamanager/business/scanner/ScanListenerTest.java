package fr.dush.mediamanager.business.scanner;

import static com.google.common.collect.Sets.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import fr.dush.mediamanager.business.configuration.producers.ScannerConfigurationProducer;
import fr.dush.mediamanager.business.mediatech.IRootDirectoryManager;
import fr.dush.mediamanager.business.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.events.scan.ScanResponseEvent;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;
import fr.dush.mediamanager.exceptions.ScanException;

public class ScanListenerTest extends SimpleJunitTest {

	@InjectMocks
	private ScanListener scanner;

	@Mock
	private Event<ScanResponseEvent> scanBus;

	@Mock
	private Instance<MoviesScanner> moviesScannerProvider;

	@Mock
	private IRootDirectoryManager rootDirectoryManager;

	@Mock
	private MoviesScanner moviesScanner;

	private ScanStatus status = new ScanStatus();

	@Before
	public void postConstruct() throws ScanException, RootDirectoryAlreadyExistsException {
		when(moviesScannerProvider.get()).thenReturn(moviesScanner);
		when(moviesScanner.startScanning(any(RootDirectory.class))).thenReturn(status);
		when(rootDirectoryManager.createOrUpdateRootDirectory(any(RootDirectory.class))).thenAnswer(new Answer<RootDirectory>() {

			@Override
			public RootDirectory answer(InvocationOnMock invocation) throws Throwable {
				return (RootDirectory) invocation.getArguments()[0];
			}
		});
	}

	@Test
	public void testScanning() throws Exception {
		final RootDirectory rootDirectory = new RootDirectory();
		rootDirectory.setEnricher("my-enricher");
		rootDirectory.setLastRefresh(null);
		rootDirectory.setMediaType(MediaType.MOVIE);
		rootDirectory.setName("Data");
		rootDirectory.setPaths(newHashSet("medias/movies", "medias/best_movies"));

		// Exec
		final ScanRequestEvent request = new ScanRequestEvent(this, rootDirectory);
		scanner.scanDirectory(request);

		// Test
		verify(moviesScannerProvider).get();
		verify(moviesScanner).startScanning(rootDirectory);
		verify(scanBus).fire(argResponseEvent(request, status));
		verify(rootDirectoryManager).createOrUpdateRootDirectory(rootDirectory);

		verifyNoMoreInteractions(moviesScannerProvider, moviesScanner, rootDirectoryManager, scanBus);
	}

	private static ScanResponseEvent argResponseEvent(final ScanRequestEvent request, final ScanStatus status2) {
		return argThat(new BaseMatcher<ScanResponseEvent>() {

			@Override
			public boolean matches(Object item) {
				if (item instanceof ScanResponseEvent) {
					assertThat(((ScanResponseEvent) item).getScanStatus()).isEqualTo(status2);
					assertThat(((ScanResponseEvent) item).getEventSource()).isEqualTo(request);

					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Bad ScanResponseEvent.");
			}
		});
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
