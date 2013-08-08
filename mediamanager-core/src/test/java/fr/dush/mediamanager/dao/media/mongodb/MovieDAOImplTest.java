package fr.dush.mediamanager.dao.media.mongodb;

import static com.google.common.collect.Sets.*;
import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;
import static org.fest.assertions.api.Assertions.*;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dto.media.SourceId;
import fr.dush.mediamanager.dto.media.video.Movie;
import fr.dush.mediamanager.dto.media.video.Person;
import fr.dush.mediamanager.dto.media.video.Trailer;
import fr.dush.mediamanager.dto.media.video.Trailers;
import fr.dush.mediamanager.dto.media.video.VideoFile;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;

@DatabaseScript(clazz = Movie.class, locations = "dataset/movies.json")
public class MovieDAOImplTest extends MongoJunitTest {

	@Inject
	private IMovieDAO movieDAO;

	private static final DateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");

	@DatabaseScript(clazz = Movie.class, inherits = false)
	@Test
	public void testPersistNewMovies() throws Exception {
		Movie m = new Movie();

		m.setTitle("Iron Man 1");
		m.getMediaIds().addId("imdb", "0123654789");
		m.getMediaIds().addId("junit", "IRONMAN_1");

		m.setGenres(newHashSet("action"));
		m.setSeen(2);
		m.setCreation(FORMATER.parse("2013-08-05"));
		m.setOtherMetaData("No other data on this films...");
		m.setOverview("Heroes aren't born. They're built.");
		m.setPoster("/some/poster.jpg");
		m.setRelease(FORMATER.parse("2008-05-01"));

		final Trailers ts = new Trailers();
		ts.setRefreshed(FORMATER.parse("2013-08-05"));
		ts.getSources().add("http://www.youtube.com/watch?v=KAE5ymVLmZg");
		final Trailer t = new Trailer();
		t.setPublishDate(FORMATER.parse("2008-05-01"));
		t.setQuality("HD1080");
		t.setSource("youtube");
		t.setTitle("Iron Man (trailer)");
		t.setTrailer(new VideoFile(Paths.get("/media/trailers/paths/ironman_1_HD_trailer.mp4")));
		t.setUrl("http://www.youtube.com/watch?v=KAE5ymVLmZg");
		ts.getTrailers().add(t);
		m.setTrailers(ts);

		m.getBackdrops().add("/media/backdrops/ironman_1.jpg");
		m.getDirectors().add(new Person("Jon Favreau", new SourceId("MovieDB", "15277")));
		m.getMainActors().add(new Person("Robert Downey Jr.", new SourceId("MovieDB", "3223")));
		m.getVideoFiles().add(new VideoFile(Paths.get("media/movies/ironman_1.mp4")));

		// Saving ...
		movieDAO.saveOrUpdateMovie(m);

		// Read
		final List<Movie> movies = movieDAO.findAll();
		assertThat(movies).hasSize(1);

		// Test...
		Movie reloaded = movies.get(0);

		assertThat(reloaded).hasTitle("Iron Man 1").hasBackdrops("/media/backdrops/ironman_1.jpg").hasSeen(2).hasGenres("action")
				.hasMediaIds(new SourceId("imdb", "0123654789"), new SourceId("junit", "IRONMAN_1"))
				.hasVideoFiles("media/movies/ironman_1.mp4");

	}

	@Test
	public void test_findUnseen() throws Exception {
		final List<Movie> movies = movieDAO.findUnseen();
		assertThat(movies).hasSize(2);

		assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TRECK");
		assertThat(movies.get(1)).hasMediaIds("junit", "IRONMAN_4");
	}

	@Test
	public void test_findByGenres() throws Exception {
		// Action only
		List<Movie> movies = movieDAO.findByGenres("action");

		assertThat(movies).hasSize(2);
		assertThat(movies.get(0)).hasMediaIds("imdb", "0123654789");
		assertThat(movies.get(1)).hasMediaIds("junit", "IRONMAN_4");

		// Action AND fantastic only
		movies = movieDAO.findByGenres("action", "fantastic");

		assertThat(movies).hasSize(1);
		assertThat(movies.get(0)).hasMediaIds("junit", "IRONMAN_4");
	}

	@Test
	public void test_findByCrew() throws Exception {
		// Robert Downey Jr
		List<Movie> movies = movieDAO.findByCrew(new SourceId("MovieDB", "3223"));

		assertThat(movies).hasSize(2);
		assertThat(movies.get(0)).hasMediaIds("imdb", "0123654789");
		assertThat(movies.get(1)).hasMediaIds("junit", "IRONMAN_4");

		// WillSmith OR J J Abrams
		movies = movieDAO.findByCrew(new SourceId("junit", "3224"), new SourceId("junit", "jjabrams"));

		assertThat(movies).hasSize(2);
		assertThat(movies.get(0)).hasMediaIds("junit", "IRONMAN_4");
		assertThat(movies.get(1)).hasMediaIds("junit", "STAR_TRECK");
	}

	@Test
	public void test_findBySourceId() throws Exception {
		final List<Movie> movies = movieDAO.findBySourceId(new SourceId("imdb", "0123654789"));
		assertThat(movies).hasSize(1);

		// Test...
		Movie reloaded = movies.get(0);

		assertThat(reloaded).hasTitle("Iron Man 1").hasBackdrops("/media/backdrops/ironman_1.jpg").hasSeen(2).hasGenres("action")
				.hasMediaIds(new SourceId("imdb", "0123654789"), new SourceId("junit", "IRONMAN_1"))
				.hasVideoFiles("media/movies/ironman_1.mp4");
	}

	@Test
	public void test_findByTitle() throws Exception {
		List<Movie> movies = movieDAO.findByTitle("Star Treck");

		assertThat(movies).hasSize(1);
		assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TRECK");

		// Full text searching...
		movies = movieDAO.findByTitle("treck");

		assertThat(movies).hasSize(1);
		assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TRECK");
	}
}
