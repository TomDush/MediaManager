package fr.dush.mediamanager.dao.mediatech.mongodb;

import static com.google.common.collect.Sets.*;
import static org.fest.assertions.api.Assertions.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

@DatabaseScript(clazz = RootDirectory.class, locations = "dataset/rootdirectory.json")
public class RootDirectoryDAOImplTest extends MongoJunitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectoryDAOImplTest.class);

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	@Test
	@DatabaseScript(clazz = RootDirectory.class, inherits = false)
	public void testSave() throws Exception {
		final RootDirectory root = new RootDirectory("My Directory", "my-scanner", "target/and-here/", "somewhere/else");
		rootDirectoryDAO.persist(root);

		final List<RootDirectory> roots = rootDirectoryDAO.findAll();
		assertThat(roots).hasSize(1);

		LOGGER.info("Found : {}", roots);

		RootDirectory found = roots.get(0);
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("My Directory");
		assertThat(found.getEnricherScanner()).isEqualTo("my-scanner");
		assertThat(found.getPaths()).containsOnly(new File("target/and-here").getAbsolutePath(),
				new File("somewhere/else").getAbsolutePath());
	}

	@Test
	public void testSave_KO() throws Exception {
		// Root path of existing RootDirectory
		try {
			final RootDirectory root = new RootDirectory("My Directory", "my-scanner", "/home/medias");
			rootDirectoryDAO.persist(root);

			failBecauseExceptionWasNotThrown(RootDirectoryAlreadyExistsException.class);

		} catch (Exception e) {
			assertThat(e).isInstanceOf(RootDirectoryAlreadyExistsException.class);
		}

		// Subpath of existing...
		try {
			final RootDirectory root = new RootDirectory("My Directory", "my-scanner", "/home/medias/movies/favorites");
			rootDirectoryDAO.persist(root);

			failBecauseExceptionWasNotThrown(RootDirectoryAlreadyExistsException.class);

		} catch (Exception e) {
			assertThat(e).isInstanceOf(RootDirectoryAlreadyExistsException.class);
		}
	}

	@Test
	public void testUpdate() throws Exception {
		final RootDirectory root = new RootDirectory("Movies", "other-movies-scanner", "/home/medias/movies/", "/external/drive/movies");
		rootDirectoryDAO.update(root);

		// Checking...
		final List<RootDirectory> roots = rootDirectoryDAO.findAll();
		assertThat(roots).isNotEmpty();

		LOGGER.info("Found : {}", roots);

		RootDirectory found = null;
		for (RootDirectory r : roots) {
			if ("Movies".equals(r.getName())) found = r;
		}

		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Movies");
		assertThat(found.getEnricherScanner()).isEqualTo("other-movies-scanner");
		assertThat(found.getPaths()).containsOnly("/home/medias/movies/", "/external/drive/movies");
	}

	@Test
	public void test_findUsingPath_OK() throws Exception {
		final List<RootDirectory> list = rootDirectoryDAO.findUsingPath(newHashSet("/home/medias/movies/favorites"));

		LOGGER.debug("List : {}", list);
		assertThat(list).hasSize(1);
		assertThat(list.get(0).getName()).isEqualTo("Movies");
	}

	@Test
	public void test_findUsingPath_KO() throws Exception {
		final List<RootDirectory> list = rootDirectoryDAO.findUsingPath(newHashSet("/mnt/home/medias/movies"));

		LOGGER.debug("List : {}", list);
		assertThat(list).isEmpty();
	}

	@Test
	public void testFindAll() throws Exception {
		final List<RootDirectory> roots = rootDirectoryDAO.findAll();
		assertThat(roots).hasSize(2);
		assertThat(extractProperty("name", String.class).from(roots)).containsOnly("Movies", "Shows");
	}

	@Test
	public void testFindBySubpath() throws Exception {
		final RootDirectory movies = rootDirectoryDAO.findBySubPath(Paths.get("/home/medias/movies/Tron - Legacy"));
		assertThat(movies).isNotNull();
		assertThat(movies.getName()).isEqualTo("Movies");
	}
}
