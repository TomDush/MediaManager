package fr.dush.mediamanager.dao.media.mongodb;

import fr.dush.mediamanager.dao.media.IMediaDAO;
import fr.dush.mediamanager.engine.MongoJunitTest;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

import javax.inject.Inject;

public class MediaDAOImplTest extends MongoJunitTest {

    @Inject
    private IMediaDAO mediaDAO;

    @Test
    public void testFindAllGenres() throws Exception {
        Assertions.assertThat(mediaDAO.findAllGenres()).isNotEmpty().contains("fantastic" , "action");
    }
}
