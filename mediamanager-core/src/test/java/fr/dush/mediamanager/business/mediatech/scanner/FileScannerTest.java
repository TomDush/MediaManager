package fr.dush.mediamanager.business.mediatech.scanner;

import static org.mockito.Mockito.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import fr.dush.mediamanager.events.scan.NewRootDirectoryEvent;

public class FileScannerTest extends SimpleJunitTest {

	@InjectMocks
	private FileScanner scanner;

	@Mock
	private IRootDirectoryDAO rootDirectoryDAO;

	@Before
	public void postConstruct() {
	}

	@Test
	public void testScanning() throws Exception {
		final RootDirectory rootDirectory = new RootDirectory();
		rootDirectory.setName("Movies Junit Database");
		rootDirectory.getPaths().add(Paths.get("/mnt/data/Films/Films vf/"));

		// Exec
		scanner.scanNewDirectory(new NewRootDirectoryEvent(this, rootDirectory));

		// Test
		verify(rootDirectoryDAO).save(rootDirectory);
	}

	@Test
	@Ignore
	public void testDateParser() throws Exception {
		String filmName = "Sherlock.Holmes.2009.CD1";

//		final Matcher m = scanner.getDatePattern().matcher(filmName);
//
//		assertThat(m.matches()).isTrue();
//		assertThat(m.group(2)).isEqualTo("2009");
	}
}
