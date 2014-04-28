package fr.dush.mediamanager.business.configuration.producers;

import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;
import static org.fest.assertions.api.Assertions.*;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.engine.CdiJunitTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME This class stop unit test with intelliJ...
@Module(name = "JUNIT Module", description = "Fake description", id = "junit-configurationmanager")
public class ConfigurationProducerTest extends CdiJunitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProducerTest.class);

	@Inject
	private ObjectMapper mapper;

	@Inject
	@Configuration(packageName = "fr.dush.mediamanager.business.foobar", definition = "configuration/config-test.json")
	private ModuleConfiguration config;

	@Test
	public void testConfigurationInjection() throws Exception {
		assertThat(config).isNotNull().hasName("JUNIT Module").hasPackageName("fr.dush.mediamanager.business.foobar").hasFieldsSize(2);
		final Field host = config.getField("host");
		assertThat(host).isNotNull().hasKey("host").hasValue("localhost").isDefaultValue();

		assertThat(config.readValue("port")).isEqualTo("8080");
		assertThat(config.readValue("foobar", "bar")).isEqualTo("bar");
		assertThat(config.readValue("foobar", "baz")).isEqualTo("baz"); // Default value isn't saved...

		final Field foo = config.getField("host");
		assertThat(foo).isDefaultValue();
		foo.setValue("baz");
		assertThat(foo).isNotDefaultValue();
	}

	@Test
	public void testGenerateObjectMapper() throws Exception {
		List<Field> fields = Lists.newArrayList(new Field("host", "localhost"), new Field("port", "8080"));
		fields.get(0).setName("Target host");
		fields.get(0).setDescription("Host on which is installed players.");
		fields.get(0).setDefaultValue(true);

        StringWriter writer = new StringWriter();
        mapper.writeValue(new WriterOutputStream(writer), fields);
        LOGGER.info("Object mapped: {}", writer.toString());
	}
}
