package fr.dush.mediamanager.business.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import lombok.Getter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Helper to read properties which can not be changed by database configuration. And parse JSON config files.
 */
public class SimpleConfiguration {

    @Getter
    private Properties properties = new Properties();

    public SimpleConfiguration(Path configFile, Integer port, String... staticFiles) {

        // Load static JSON config as default value
        for (String file : staticFiles) {
            FieldSet fieldSet = readFieldSetFile(file);
            fieldSet.loadMap(properties);

        }

        // Load properties file
        try {
            properties.load(new FileInputStream(configFile.toFile()));
        } catch (IOException e) {
            throw new ConfigurationException(e, "Configuration file %s doesn't exist: %s", configFile, e.getMessage());
        }

        // Load direct values
        if (port != null) {
            properties.setProperty("remotecontrol.port", port.toString());
        }

        properties.setProperty("staticFiles", Joiner.on(",").join(staticFiles));
    }

    /**
     * Read field set from JSON file and override it with environment variables
     *
     * @param mapper   JSON Mapper to deserialise file
     * @param fileName File name, without repertory or extension.
     */
    public static FieldSet readFieldSetFile(ObjectMapper mapper, String fileName) {

        String filePath = "configuration/" + fileName + ".json";

        try {
            Resource resource = new DefaultResourceLoader().getResource(filePath);
            FieldSet fieldSet = mapper.readValue(resource.getInputStream(), FieldSet.class);
            fieldSet.setConfigId(fileName);

            // Override properties with system and env values.
            overrideWithEnvironmentVariables(fieldSet);

            return fieldSet;

        } catch (IOException e) {
            throw new ConfigurationException("Configuration file %s doesn't exist in classpath.", filePath, e);
        }
    }

    /**
     * Read field set from JSON file and override it with environment variables
     *
     * @param fileName File name, without repertory or extension.
     */
    public static FieldSet readFieldSetFile(String fileName) {
        return readFieldSetFile(new ObjectMapper(), fileName);
    }

    /** Override all values with those set in System environment. */
    private static void overrideWithEnvironmentVariables(FieldSet fieldSet) {
        Map<String, String> env = System.getenv();

        for (Field f : fieldSet.getFields()) {
            final String propKey = fieldSet.getConfigId() + "." + f.getKey();

            String value = env.get(propKey);
            if (isEmpty(value)) {
                value = System.getProperties().getProperty(propKey);
            }

            if (isNotEmpty(value)) {
                f.setValue(value);
            }
        }
    }
}
