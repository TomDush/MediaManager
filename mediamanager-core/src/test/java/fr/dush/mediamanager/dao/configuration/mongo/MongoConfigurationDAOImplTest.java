package fr.dush.mediamanager.dao.configuration.mongo;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.engine.MongoJunitTest;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;

@DatabaseScript(clazz = FieldSet.class, collectionName = "Config", locations = "dataset/config.json")
public class MongoConfigurationDAOImplTest extends MongoJunitTest {

    private static final String COLLECTION_NAME = "Config";

    @Inject
    private MongoConfigurationDAOImpl mongoConfigurationDAO;

    @Test
    @DatabaseScript(clazz = FieldSet.class, collectionName = COLLECTION_NAME, clear = true, inherits = false)
    public void testSaveAndRead() throws Exception {
        final FieldSet set = new FieldSet();
        set.setName("Configuration name");
        set.setConfigId("junit.dao.config");

        set.addValue("working", "true", false);
        set.addValue("testCount", "42", true);

        // Saving
        mongoConfigurationDAO.save(set);

        final DBCursor configs = getCollection(COLLECTION_NAME).find();
        Assertions.assertThat(configs.count()).isEqualTo(1);
        final DBObject dbObject = configs.next();
        Assertions.assertThat(dbObject.get("_id")).isEqualTo("junit.dao.config");

        // Read
        final List<Field> list = mongoConfigurationDAO.findByPackage("junit.dao.config");
        Assertions.assertThat(list).hasSize(2);
        for (Field f : list) {
            if ("working".equals(f.getKey())) {
                assertThat(f).hasKey("working").hasValue("true");
            } else if ("testCount".equals(f.getKey())) {
                assertThat(f).hasKey("testCount").hasValue("42");
            }
        }
    }

    @Test
    public void testRead() throws Exception {
        final List<Field> list = mongoConfigurationDAO.findByPackage("junit.notExisting");
        Assertions.assertThat(list).isNotNull().isEmpty();

        final List<Field> list2 = mongoConfigurationDAO.findByPackage("junit.answers");
        Assertions.assertThat(list2).hasSize(2);
        for (Field f : list2) {
            if ("TheAnswer".equals(f.getKey())) {
                assertThat(f).hasValue("42");
            } else if ("season".equals(f.getKey())) {
                assertThat(f).hasValue("winter is coming");
            }
        }
    }
}
