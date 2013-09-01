package fr.dush.mediamanager.dao.configuration.file;

import static org.fest.assertions.api.Assertions.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;

@RunWith(BlockJUnit4ClassRunner.class)
public class FileConfigurationDAOImplTest {

	private String previousValue;

	@Test
	public void testReadPropertiesFile() throws Exception {
		previousValue = System.setProperty("mediamanager.propertiesfile", "src/test/resources/mainconfig.properties");

		FileConfigurationDAOImpl configurationDAO = new FileConfigurationDAOImpl();
		configurationDAO.initialize();

		final List<Field> fields = configurationDAO.findByPackage("junit");
		FieldSet set = new FieldSet();
		set.addAllFields(fields, true);

		assertThat(set.getFields().get("activated").getValue()).isEqualTo("true");
		assertThat(set.getFields().get("visible")).isNull();

	}

	@After
	public void restorePreviousValue() {
		if(previousValue != null) {
			System.setProperty("mediamanager.propertiesfile", previousValue);
		}
	}
}
