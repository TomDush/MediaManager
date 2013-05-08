package fr.dush.mediamanager.business.configuration.impl;

import static com.google.common.collect.Lists.*;
import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;
import static org.fest.assertions.api.Assertions.*;

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;
import fr.dush.mediamanager.engine.CdiJunitTest;

@Default
@Module(name = "JUNIT Module", description = "Fake description")
public class ConfigurationManagerImplTest extends CdiJunitTest {

	@Inject
	private IConfigurationManager configurationManager;

	@Inject
	private ObjectMapper mapper;

	@Inject
	@Configuration(packageName = "fr.dush.mediamanager.business.foobar", definition = "configuration/config-test.json")
	private ModuleConfiguration config;

	@Test
	public void testName() throws Exception {
		assertThat(configurationManager).isNotNull().isInstanceOf(ConfigurationManagerImpl.class);
	}

	@Test
	public void testConfigurationInjection() throws Exception {
		assertThat(config).isNotNull().hasName("JUNIT Module").hasPackageName("fr.dush.mediamanager.business.foobar").hasFieldsSize(2);
		final Field host = config.getField("host");
		assertThat(host).isNotNull().hasKey("host").hasValue("localhost").isDefaultValue();

		assertThat(config.getValue("port")).isEqualTo("8080");
		assertThat(config.getValue("foo", "bar")).isEqualTo("bar");
		assertThat(config.getValue("foo", "baz")).isEqualTo("bar"); // Default value is saved...

		final Field foo = config.getField("foo");
		assertThat(foo).isDefaultValue();
		foo.setValue("baz");
		assertThat(foo).isNotDefaultValue();
	}

	@Test
	public void testGenerateObjectMapper() throws Exception {
		List<Field> fields = newArrayList(new Field("host", "localhost"), new Field("port", "8080"));
		fields.get(0).setName("Target host");
		fields.get(0).setDescription("Host on which is installed players.");
		fields.get(0).setDefaultValue(true);

		mapper.writeValue(System.out, fields);
	}

	@Test
	public void testResolveProperties() throws Exception {
		final ModuleConfiguration generic = new ModuleConfiguration(null, new FieldSet("generic"));
		ModuleConfiguration config = new ModuleConfiguration(generic, new FieldSet("foobar"));

		final String temp = config.getValue("temp", "temp_dir");
		assertThat(temp).isEqualTo("temp_dir");

		generic.addField(new Field("mediamanager.root", "${user.home}/.mediamanager"));
		final String root = System.getProperty("user.home") + "/.mediamanager";
		assertThat(generic.getValue("mediamanager.root")).isEqualTo(root);

		final String value = config.getValue("foobar", "${mediamanager.root}/${temp}");
		assertThat(value).isEqualTo(root + "/temp_dir");
	}
}
