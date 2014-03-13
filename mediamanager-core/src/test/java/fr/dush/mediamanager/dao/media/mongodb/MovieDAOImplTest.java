package fr.dush.mediamanager.dao.media.mongodb;

import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.media.queries.*;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.video.*;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import fr.dush.mediamanager.tools.PathsUtils;
import org.bson.types.ObjectId;
import org.junit.Test;

import javax.inject.Inject;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Sets.*;
import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;

@DatabaseScript(clazz = Movie.class, locations = "dataset/movies.json")
public class MovieDAOImplTest extends MongoJunitTest {

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private IMovieDAO movieDAO;

    @DatabaseScript(clazz = Movie.class, inherits = false)
    @Test
    public void testPersistNewMovies() throws Exception {
        Movie m = new Movie();

        m.setTitle("Iron Man 1");
        m.getMediaIds().addId("imdb", "0123654789");
        m.getMediaIds().addId("junit", "IRONMAN_1");

        m.setGenres(newHashSet("action"));
        m.setSeen(2);
        m.setCreation(FORMATTER.parse("2013-08-05"));
        m.setOtherMetaData("No other data on this films...");
        m.setOverview("Heroes aren't born. They're built.");
        m.setPoster("/some/poster.jpg");
        m.setRelease(FORMATTER.parse("2008-05-01"));

        final Trailers ts = new Trailers();
        ts.setRefreshed(FORMATTER.parse("2013-08-05"));
        ts.getSources().add("MOVIES_DB");
        final Trailer t = new Trailer();
        t.setPublishDate(FORMATTER.parse("2008-05-01"));
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

        // 1ST EXEC : saving ...
        movieDAO.saveOrUpdateMovie(m);

        // Tests ...
        List<Movie> movies = movieDAO.findAll();
        assertThat(movies).hasSize(1);

        Movie reloaded = movies.get(0);

        assertThat(reloaded).hasTitle("Iron Man 1")
                            .hasBackdrops("/media/backdrops/ironman_1.jpg")
                            .hasGenres("action")
                            .hasMediaIds(new SourceId("imdb", "0123654789"), new SourceId("junit", "IRONMAN_1"))
                            .hasVideoFiles(Paths.get("media/movies/ironman_1.mp4")
                                                .toAbsolutePath()
                                                .normalize()
                                                .toString());
        assertThat(reloaded).hasSeen(2);

        // ** Changes ...
        m.setTitle("Iron Man");
        m.setOverview("No overview...");
        m.setPoster("/media/posters/ironman_1_poster.jpg");
        m.setSeen(22);

        // 2ST EXEC : update ...
        movieDAO.saveOrUpdateMovie(m);

        // Tests ...
        movies = movieDAO.findAll();
        assertThat(movies).hasSize(1);

        reloaded = movies.get(0);
        assertThat(reloaded).hasTitle("Iron Man")
                            .hasOverview("No overview...")
                            .hasPoster("/media/posters/ironman_1_poster.jpg");
        assertThat(reloaded).hasSeen(2); // no changed : read-only
    }

    @Test
    public void test_updateExisting() throws Exception {
        // Test adding video and genre...
        Movie m = new Movie();

        m.setTitle("Star Trek - The future begins");
        m.setOverview("The future begins");

        m.getMediaIds().addId("MoviesDB", "13475");
        m.getMediaIds().addId("junit", "STAR_TREK");

        m.setGenres(newHashSet("science fiction", "action", "adventure"));
        m.getVideoFiles().add(new VideoFile(Paths.get("/media/movies/HD/star_trek_1.mkv")));

        // EXEC
        movieDAO.saveOrUpdateMovie(m);

        // TEST
        final Movie movie = movieDAO.findById(new ObjectId("5200c7a884ae0d25732cd70c"));
        assertThat(movie).hasTitle("Star Trek - The future begins")
                         .hasOverview("The future begins")
                         .hasMediaIds("MoviesDB", "13475", "junit", "STAR_TREK")
                         .hasVideoFiles(PathsUtils.toAbsolute("/media/movies/HD/star_trek_1.mkv"),
                                        "/media/movies/star_trek.mp4");
    }

    @Test
    public void test_incViewCount() throws Exception {
        final Movie movie = movieDAO.findById(new ObjectId("5200c7a884ae0d25732cd70a"));
        assertThat(movie).as("DataTest invalid").isNotNull();

        // Exec
        movieDAO.incrementViewCount(movie.getId(), 2);

        // Test
        final Movie reloaded = movieDAO.findById(new ObjectId("5200c7a884ae0d25732cd70a"));
        assertThat(reloaded).hasSeen(4);

        // ** Other test (field doesn't exist)
        final Movie movie2 = movieDAO.findById(new ObjectId("5200c7a884ae0d25732cd70b"));
        assertThat(movie2).as("DataTest invalid").isNotNull();

        // Exec
        movieDAO.incrementViewCount(movie2.getId(), 1);

        // Test
        final Movie reloaded2 = movieDAO.findById(new ObjectId("5200c7a884ae0d25732cd70b"));
        assertThat(reloaded2).hasSeen(1);
    }

    @Test
    public void test_findUnseen() throws Exception {
        final List<Movie> movies = movieDAO.findUnseen();
        assertThat(movies).hasSize(2);

        assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TREK");
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
        assertThat(movies.get(1)).hasMediaIds("junit", "STAR_TREK");
    }

    @Test
    public void test_findBySourceId() throws Exception {
        final List<Movie> movies = movieDAO.findBySourceId(new SourceId("imdb", "0123654789"));
        assertThat(movies).hasSize(1);

        // Test...
        Movie reloaded = movies.get(0);

        assertThat(reloaded).hasTitle("Iron Man 1")
                            .hasBackdrops("/media/backdrops/ironman_1.jpg")
                            .hasSeen(2)
                            .hasGenres("action")
                            .hasMediaIds(new SourceId("imdb", "0123654789"), new SourceId("junit", "IRONMAN_1"))
                            .hasVideoFiles("media/movies/ironman_1.mp4");
    }

    @Test
    public void test_findByTitle() throws Exception {
        List<Movie> movies = movieDAO.findByTitle("Star Trek");

        assertThat(movies).hasSize(1);
        assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TREK");

        // Full text searching...
        movies = movieDAO.findByTitle("trek");

        assertThat(movies).hasSize(1);
        assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TREK");

        // With pagination
        movies = movieDAO.search(new SearchForm("trek"), new SearchLimit(1, 10), Order.LIST).getList();

        assertThat(movies).hasSize(1);
        assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TREK");
    }

    @Test
    public void test_fullSearch() {
        List<Movie> movies = movieDAO.search(new SearchForm("Star Trek"), null).getList();

        assertThat(movies).hasSize(1);
        assertThat(movies.get(0)).hasMediaIds("junit", "STAR_TREK");
    }

    @Test
    public void test_random() throws Exception {
        PaginatedList<Movie> movies = movieDAO.search(new SearchForm(), new SearchLimit(3), Order.RANDOM);
        assertThat(new HashSet<>(movies.getList())).hasSize(3);

        movies = movieDAO.search(new SearchForm(), new SearchLimit(1), Order.RANDOM);
        assertThat(new HashSet<>(movies.getList())).hasSize(1);
    }

    @Test
    public void testExistingFieldConstraints() throws Exception {
        SearchForm form = new SearchForm();
        form.getNotNullFields().add(MediaField.BACKDROPS);
        form.getNotNullFields().add(MediaField.OVERVIEW);
        form.getNotNullFields().add(MediaField.POSTER);
        form.getNotNullFields().add(MediaField.TRAILER);
        form.getNotNullFields().add(MediaField.YEAR);

        PaginatedList<Movie> movies = movieDAO.search(form, new SearchLimit(), Order.ALPHA);
        System.out.println(movies);

        assertThat(movies.getList()).hasSize(1);
    }
}
