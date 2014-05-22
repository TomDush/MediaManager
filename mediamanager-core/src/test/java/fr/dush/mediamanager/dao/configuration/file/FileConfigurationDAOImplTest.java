package fr.dush.mediamanager.dao.configuration.file;

import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class FileConfigurationDAOImplTest {

    private String previousValue;

    @Test
    public void testReadPropertiesFile() throws Exception {
        previousValue =
                System.setProperty("mediamanager.propertiesfile", "src/test/resources/dbconfig-junit.properties");

        FileConfigurationDAOImpl configurationDAO = new FileConfigurationDAOImpl();
        configurationDAO.initialize();

        final List<Field> fields = configurationDAO.findByPackage("junit");
        FieldSet set = new FieldSet();
        set.addAllFields(fields, true);

        assertThat(set.getFieldMap().get("activated").getValue()).isEqualTo("true");
        assertThat(set.getFieldMap().get("visible")).isNull();

    }

    @After
    public void restorePreviousValue() {
        if (previousValue != null) {
            System.setProperty("mediamanager.propertiesfile", previousValue);
        }
    }
}
