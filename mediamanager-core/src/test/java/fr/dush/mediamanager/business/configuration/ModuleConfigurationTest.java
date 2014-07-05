package fr.dush.mediamanager.business.configuration;

import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.exceptions.PropertyUnresolvableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModuleConfigurationTest {

    @Mock
    private IConfigurationManager configurationManager;

    @Test
    public void testResolveProperties() throws Exception {
        // ** Tests on a single Module configuration
        when(configurationManager.getModuleConfiguration("user")).thenThrow(new ConfigurationException(
                "Expected exception"));
        when(configurationManager.getModuleConfiguration("somewhereElse")).thenThrow(new ConfigurationException(
                "Expected exception"));

        final ModuleConfiguration generic =
                new ModuleConfiguration(configurationManager, "mediamanager", new FieldSet("mediamanager"));
        generic.addField(new Field("host", "localhost", false));
        generic.addField(new Field("port", "80", true));
        generic.addField(new Field("local", "true", false));
        generic.addField(new Field("server", "Local Server", true));
        generic.addField(new Field("root", "${user.home}/.mediamanager"));
        generic.addField(new Field("secret", null, true));

        // Assert on this single
        assertThat(generic.readValue("host")).isEqualTo("localhost");
        assertThat(generic.readValue("mediamanager.host")).isEqualTo("localhost");
        assertThat(generic.readValueAsInt("port")).isEqualTo(80);
        assertThat(generic.readValueAsBoolean("local")).isTrue();
        assertThat(generic.readValue("server")).isEqualTo("Local Server");
        assertThat(generic.readValue("host", "foobar")).isEqualTo("localhost");
        assertThat(generic.readValue("server", "foobar")).isEqualTo("foobar");

        assertThat(generic.readValue("secret", "foobar")).isEqualTo("foobar");
        assertThat(generic.readValue("secret")).isNull();

        // Failures...
        try {
            generic.readValue("toto"); // Is not defined
            failBecauseExceptionWasNotThrown(PropertyUnresolvableException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(PropertyUnresolvableException.class);
        }

        // ** Test with another module
        when(configurationManager.getModuleConfiguration("mediamanager")).thenReturn(generic);

        ModuleConfiguration config = new ModuleConfiguration(configurationManager, "proxy", new FieldSet("proxy"));
        config.addField(new Field("port", "80", true));
        config.addField(new Field("rootpath", null, true));

        // Read Complex...
        final String expectedRoot = System.getProperty("user.home") + "/.mediamanager";
        assertThat(generic.readValue("mediamanager.root")).isEqualTo(expectedRoot);

        // Read non-existing
        try {
            config.readValue("somewhereElse.temp");
            failBecauseExceptionWasNotThrown(PropertyUnresolvableException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(PropertyUnresolvableException.class);
        }

        // Read with properties
        Properties props = new Properties();
        props.put("mediamanager.host", "mediamanagerserver");
        props.put("port", "8090"); // Useless because no prefix
        props.put("proxy.port", "8080");
        props.put("proxy.rootpath", "index.xhtml");

        final String resolved = config.resolveProperties(
                "http://${mediamanager.host}:${port}/${rootpath}?path=${mediamanager.root}",
                props);
        assertThat(resolved).isEqualTo("http://localhost:8080/index.xhtml?path=" + expectedRoot);

    }

}
