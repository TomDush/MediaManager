package fr.dush.mediamanager.dao.configuration.file;

import static org.fest.assertions.api.Assertions.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;

@RunWith(BlockJUnit4ClassRunner.class)
public class FileConfigurationDAOImplTest {

	@Test
	public void testReadPropertiesFile() throws Exception {
		System.setProperty("mediamanager.propertiesfile", "src/test/resources/mainconfig.properties");

		FileConfigurationDAOImpl configurationDAO = new FileConfigurationDAOImpl();
		configurationDAO.initialize();

		final List<Field> fields = configurationDAO.findByPackage("junit");
		FieldSet set = new FieldSet();
		set.addAllFields(fields, true);

		assertThat(set.getFields().get("activated").getValue()).isEqualTo("true");
		assertThat(set.getFields().get("visible")).isNull();

	}
}
