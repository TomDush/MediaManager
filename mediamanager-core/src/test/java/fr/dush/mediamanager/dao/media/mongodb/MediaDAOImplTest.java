package fr.dush.mediamanager.dao.media.mongodb;

import fr.dush.mediamanager.dao.media.IMediaDAO;
import fr.dush.mediamanager.domain.media.video.Movie;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;

@DatabaseScript(clazz = Movie.class, locations = "dataset/movies.json")
public class MediaDAOImplTest extends MongoJunitTest {

    @Inject
    private IMediaDAO mediaDAO;

    @Test
    public void testFindAllGenres() throws Exception {
        assertThat(mediaDAO.findAllGenres()).isNotEmpty().contains("fantastic", "action");
    }
}
