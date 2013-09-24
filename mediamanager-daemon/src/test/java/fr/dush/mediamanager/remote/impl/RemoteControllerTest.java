package fr.dush.mediamanager.remote.impl;

import static com.google.common.collect.Lists.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.util.List;

import javax.enterprise.event.Event;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.dush.mediamanager.business.configuration.IConfigurationRegister;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.scanner.IScanRegister;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.configuration.FieldSet;
import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.dto.tree.MediaType;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.ConfigurationFieldAssert;
import fr.dush.mediamanager.remote.Stopper;

@RunWith(BlockJUnit4ClassRunner.class)
public class RemoteControllerTest {

	@InjectMocks
	private RemoteController remoteController;

	@Mock
	private Stopper stopper;

	@Mock
	private IRootDirectoryDAO rootDirectoryDAO;

	@Mock
	private Event<ScanRequestEvent> requestBus;

	@Mock
	private IConfigurationRegister configurationRegister;

	@Mock
	private IScanRegister scanRegister;

	@Mock
	private IMovieDAO movieDAO;

	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFullConfiguration() throws Exception {
		final FieldSet f1 = new FieldSet("package1");
		f1.addValue("foobar", "foo", false);
		f1.getFields().get("foobar").setDescription("Foobar desc");
		f1.addValue("bar", "baz", false);
		final ModuleConfiguration m1 = new ModuleConfiguration(null, f1);

		final FieldSet f2 = new FieldSet("package2");
		f2.addValue("bar", "toto", false);
		final ModuleConfiguration m2 = new ModuleConfiguration(null, f2);
		when(configurationRegister.findAll()).thenReturn(newArrayList(m1, m2));

		// Exec
		final List<ConfigurationField> configs = remoteController.getFullConfiguration();

		// Check
		assertThat(configs).hasSize(3);
		ConfigurationFieldAssert.assertThat(configs.get(0)).hasFullname("package1.bar").hasValue("baz");
		ConfigurationFieldAssert.assertThat(configs.get(1)).hasFullname("package1.foobar").hasValue("foo").hasDescription("Foobar desc");
		ConfigurationFieldAssert.assertThat(configs.get(2)).hasFullname("package2.bar").hasValue("toto");
	}

	@Test
	public void test_getInprogressScanning() throws Exception {
		final ScanStatus s1 = new ScanStatus();
		final ScanStatus s2 = new ScanStatus();
		when(scanRegister.getInprogressScans()).thenReturn(newArrayList(s1, s2));

		// Exec
		final List<ScanStatus> scans = remoteController.getInprogressScanning();

		// Check
		assertThat(scans).containsOnly(s1, s2);
	}

	@Test
	public void test_scan_OK() throws Exception {
		final ScanStatus s = new ScanStatus();
		when(scanRegister.waitResponseFor(any(ScanRequestEvent.class))).thenReturn(s);

		// Exec
		remoteController.scan(MediaType.MOVIE, "/media/movies", "imdb-enricher");

		// Check
		verify(requestBus).fire(argScanRequestEvent(MediaType.MOVIE, "movies", "imdb-enricher", "/media/movies"));
		verify(scanRegister).waitResponseFor(argScanRequestEvent(MediaType.MOVIE, "movies", "imdb-enricher", "/media/movies"));

	}

	private final ScanRequestEvent argScanRequestEvent(final MediaType mediaType, final String name, final String enricher,
			final String... paths) {
		return argThat(new BaseMatcher<ScanRequestEvent>() {

			private String error = "";

			@Override
			public boolean matches(Object item) {
				if (item instanceof ScanRequestEvent) {
					ScanRequestEvent event = (ScanRequestEvent) item;
					try {
						final RootDirectory rd = new RootDirectory(name, mediaType, paths);
						rd.setEnricher(enricher);
						assertThat(event.getRootDirectory()).isEqualsToByComparingFields(rd);

						return true;
					} catch (Error e) {
						error = e.getMessage();
						return false;
					}

				}

				error = "Is not instance of ScanRequestEvent";
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(error);
			}
		});
	}

	@Test
	public void test_scan_KO() throws Exception {
		try {
			remoteController.scan(MediaType.MOVIE, "/media/movies", "imdb-enricher");
			failBecauseExceptionWasNotThrown(RemoteException.class);

		} catch (Exception e) {
			assertThat(e).isInstanceOf(RemoteException.class).hasMessageContaining("No response received");
		}
	}

}
