package fr.dush.mediamanager.dao.mediatech.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.domain.media.*;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import org.bson.types.ObjectId;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Thomas Duchatelle
 */
@DatabaseScript(clazz = Recovery.class, locations = "dataset/recoveries.json")
public class RecoveryDAOImplTest extends MongoJunitTest {

    public static final String MOVIE_ID = "5240760958eff5a9e1d18203";

    @Inject
    private IRecoveryDAO recoveryDAO;

    @Inject
    private ObjectMapper mapper;

    @Test
    @DatabaseScript(clazz = Recovery.class, clear = true)
    public void testSave() throws Exception {
        Recovery recovery = newRecovery();

        // Exec
        recoveryDAO.save(recovery);

        // Reload
        List<Recovery> all = recoveryDAO.findAll();
        assertThat(all).hasSize(1);
        Assertions.assertThat(all.get(0)).hasId(new MediaReference(MediaType.MOVIE, new ObjectId(MOVIE_ID)));

    }

    @Test
    public void testLoad() throws Exception {
        Recovery r = recoveryDAO.findById(new MediaReference(MediaType.MOVIE, MOVIE_ID));
        System.out.println("Reference: " + mapper.writeValueAsString(new MediaReference(MediaType.MOVIE, MOVIE_ID)));

        // Assert
        Assertions.assertThat(r)
                  .hasId(new MediaReference(MediaType.MOVIE, new ObjectId(MOVIE_ID)))
                  .hasMediaFiles("ironman.avi")
                  .hasPosition(5);

    }

    @Test
    public void testUpdate() throws Exception {
        Recovery recovery = newRecovery();

        // Exec
        recoveryDAO.save(recovery);

        // Reload
        List<Recovery> all = recoveryDAO.findAll();
        assertThat(all).hasSize(1);
        Assertions.assertThat(all.get(0))
                  .hasId(new MediaReference(MediaType.MOVIE, MOVIE_ID))
                  .hasMediaFiles("ironman_CD1.avi", "ironman_CD2.avi")
                  .hasPosition(14);

    }

    @Test
    public void testDelete() throws Exception {
        // Exec
        recoveryDAO.delete(new MediaReference(MediaType.MOVIE, MOVIE_ID));

        // Reload
        List<Recovery> all = recoveryDAO.findAll();
        assertThat(all).isEmpty();

    }

    private Recovery newRecovery() {
        MediaSummary summary = new MediaSummary(MediaType.MOVIE, MOVIE_ID);

        Recovery recovery = new Recovery(summary);
        recovery.setMediaFiles(newArrayList("ironman_CD1.avi", "ironman_CD2.avi"));
        recovery.setLength(100);
        recovery.setPosition(14);

        return recovery;
    }
}
