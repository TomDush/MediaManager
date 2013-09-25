package fr.dush.mediamanager.business.configuration;

import static org.fest.assertions.api.Assertions.*;

import java.util.Properties;

import org.junit.Test;

import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;

public class ModuleConfigurationTest {

	@Test
	public void testResolveProperties() throws Exception {
		final ModuleConfiguration generic = new ModuleConfiguration(null, new FieldSet("generic"));
		generic.addField(new Field("host", "localhost", true));
		generic.addField(new Field("mediamanager.root", "${user.home}/.mediamanager"));

		ModuleConfiguration config = new ModuleConfiguration(generic, new FieldSet("foobar"));
		config.addField(new Field("port", "80", false));

		// Read existing
		final String root = System.getProperty("user.home") + "/.mediamanager";
		assertThat(generic.readValue("mediamanager.root")).isEqualTo(root);

		// Read non-existing
		final String temp = config.readValue("temp", "temp_dir");
		assertThat(temp).isEqualTo("temp_dir");

		// Read with properties
		Properties props = new Properties();
		props.put("host", "mediamanagerserver");
		props.put("port", "8080");
		props.put("rootpath", "index.xhtml");

		final String resolved = config.resolveProperties("http://${host}:${port}/${rootpath}?path=${mediamanager.root}", props);
		assertThat(resolved).isEqualTo("http://mediamanagerserver:80/index.xhtml?path=" + root);

	}

	@Test
	public void testGetPropertyPriority() throws Exception {
		final ModuleConfiguration generic = new ModuleConfiguration(null, new FieldSet("generic"));
		ModuleConfiguration config = new ModuleConfiguration(generic, new FieldSet("foobar"));

		assertThat(config.readValue("foo")).isEqualTo(null);

		generic.addField(new Field("foo", "genericDefault", true));
		assertThat(config.readValue("foo")).isEqualTo("genericDefault");

		config.addField(new Field("foo", "specificDefault", true));
		assertThat(config.readValue("foo")).isEqualTo("specificDefault");

		assertThat(config.readValue("foo", "givenDefault")).isEqualTo("givenDefault");

		System.setProperty("foo", "systemValue");
		assertThat(config.readValue("foo", "givenDefault")).isEqualTo("systemValue");

		generic.addField(new Field("foo", "genericValue", false));
		assertThat(config.readValue("foo", "givenDefault")).isEqualTo("genericValue");

		config.addField(new Field("foo", "specificValue", false));
		assertThat(config.readValue("foo", "givenDefault")).isEqualTo("specificValue");


	}
}
