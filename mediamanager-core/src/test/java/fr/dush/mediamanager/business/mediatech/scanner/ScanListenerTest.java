package fr.dush.mediamanager.business.mediatech.scanner;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import fr.dush.mediamanager.business.configuration.producers.ScannerConfigurationProducer;
import fr.dush.mediamanager.business.mediatech.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.engine.mock.EventMock;
import fr.dush.mediamanager.events.scan.reponses.InprogressScanningResponseEvent;
import fr.dush.mediamanager.events.scan.request.NewRootDirectoryEvent;

public class ScanListenerTest extends SimpleJunitTest {

	@InjectMocks
	private ScanListener scanner;

	@Mock
	private Instance<MoviesScanner> moviesScannerProvider;

	@Mock
	private MoviesScanner moviesScanner;

	@Mock
	private IRootDirectoryDAO rootDirectoryDAO;

	@Spy
	private EventMock<InprogressScanningResponseEvent> bus = new EventMock<InprogressScanningResponseEvent>();

	@Before
	public void postConstruct() {
		when(moviesScannerProvider.get()).thenReturn(moviesScanner);
	}

	@Test
	public void testScanning() throws Exception {
		final RootDirectory rootDirectory = new RootDirectory();

		// Exec
		scanner.scanNewDirectory(new NewRootDirectoryEvent(this, rootDirectory));

		// Test
		assertThat(bus.getEvents()).isNotEmpty().hasSize(1);
		verify(moviesScannerProvider).get();
		verify(moviesScanner).startScanning(rootDirectory);
		verify(rootDirectoryDAO).persist(rootDirectory);

		verifyNoMoreInteractions(moviesScannerProvider, moviesScanner, rootDirectoryDAO);
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
