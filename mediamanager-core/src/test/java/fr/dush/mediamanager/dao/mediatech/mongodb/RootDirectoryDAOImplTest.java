package fr.dush.mediamanager.dao.mediatech.mongodb;

import static com.google.common.collect.Sets.*;
import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;
import static org.fest.assertions.api.Assertions.*;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.MediaType;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;

@DatabaseScript(clazz = RootDirectory.class, locations = "dataset/rootdirectory.json")
public class RootDirectoryDAOImplTest extends MongoJunitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectoryDAOImplTest.class);

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	@Test
	@DatabaseScript(clazz = RootDirectory.class, inherits = false)
	public void testSave() throws Exception {
		final RootDirectory root = new RootDirectory("My Directory", MediaType.MOVIE, "target/and-here/", "somewhere/else");
		root.setEnricher("my-scanner");
		rootDirectoryDAO.saveOrUpdate(root);

		final List<RootDirectory> roots = rootDirectoryDAO.findAll();
		assertThat(roots).hasSize(1);

		LOGGER.info("Found : {}", roots);

		RootDirectory found = roots.get(0);
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("My Directory");
		assertThat(found.getMediaType()).isEqualTo(MediaType.MOVIE);
		assertThat(found.getEnricher()).isEqualTo("my-scanner");
		// Path are kept like there are...
		assertThat(found.getPaths()).containsOnly("target/and-here/", "somewhere/else");
	}

	@Test
	public void testUpdate() throws Exception {
		final RootDirectory root = new RootDirectory("Movies", MediaType.MOVIE, "/home/medias/movies/", "/external/drive/movies");
		root.setEnricher("other-movies-scanner");
		rootDirectoryDAO.saveOrUpdate(root);

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
		assertThat(found.getEnricher()).isEqualTo("other-movies-scanner");
		assertThat(found.getMediaType()).isEqualTo(MediaType.MOVIE);
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

	@Test
	public void testFindById() throws Exception {
		final RootDirectory root = rootDirectoryDAO.findById("Movies");
		assertThat(root).isNotNull().hasName("Movies");
	}

	@Test
	public void testLastUpdateDate() throws Exception {
		final Date date = new Date();
		rootDirectoryDAO.markAsUpdated("Movies", date);

		final RootDirectory root = rootDirectoryDAO.findById("Movies");
		assertThat(root).isNotNull().hasName("Movies").hasLastRefresh(date);
	}
}
