package fr.dush.mediamanager.business.configuration.producers;

import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.engine.SpringJUnitTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.dush.mediamanager.engine.festassert.configuration.MediaManagerAssertions.*;

public class ConfigurationManagerImplTest extends SpringJUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerImplTest.class);

    @Config(id = "foobar")
    private ModuleConfiguration foobarConfig;

    @Test
    public void testConfigurationInjection() throws Exception {
        assertThat(foobarConfig).isNotNull().hasName("JUNIT Module").hasFieldsSize(2);
        final Field host = foobarConfig.getField("host");
        assertThat(host).isNotNull().hasKey("host").hasValue("localhost").isDefaultValue();

        Assertions.assertThat(foobarConfig.readValue("host")).isEqualTo("localhost");
        Assertions.assertThat(foobarConfig.readValue("foobar.host")).isEqualTo("localhost");

        Assertions.assertThat(foobarConfig.readValue("host", "medima-server")).isEqualTo("medima-server");
    }

    @Test
    public void testChangeFieldValue() throws Exception {
        Field field = new Field("foo", "baz", true);
        assertThat(field).isDefaultValue();

        field.setValue("toto");
        assertThat(field).isNotDefaultValue();
    }
}
