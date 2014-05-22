package fr.dush.mediamanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * Spring Java Based Configuration for Medima application. Provide a property place holder based on application
 * properties file (defined in Spring environment) and default packaged properties (in JSON files).
 */
@Configuration
@ComponentScan(basePackages = "fr.dush.mediamanager")
public class SpringConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);

    @Bean
    public ModuleConfiguration getMockModuleConfiguration() {
        return new ModuleConfiguration(null, new FieldSet());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer(ApplicationContext
                                                                                                          applicationContext) {

        try {
            PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();

            // Load file (has first priority)
            if (applicationContext.getEnvironment().containsProperty("mediamanager.propertiesfile")) {
                String propertyFile = applicationContext.getEnvironment()
                                                        .resolveRequiredPlaceholders(
                                                                "file:${mediamanager.propertiesfile}");
                p.setLocations(new Resource[]{applicationContext.getResource(propertyFile)});
            }

            // Load required properties from default configuration (JSON files)
            p.setProperties(loadPropertiesFromJson(applicationContext, "mongodb"));

            return p;

        } catch (IOException e) {
            throw new ConfigurationException(e, "Could not load property placeholder: %s.", e.getMessage());
        }
    }

    private static Properties loadPropertiesFromJson(ApplicationContext applicationContext,
                                                     String... files) throws IOException {
        Properties props = new Properties();

        for (String file : files) {
            Resource resource = applicationContext.getResource("configuration/" + file + ".json");
            ObjectMapper mapper = new ObjectMapper();
            FieldSet fieldSet = mapper.readValue(resource.getInputStream(), FieldSet.class);

            for (Field field : fieldSet.getFields()) {
                props.put(file + "." + field.getKey(), field.getValue());
            }
        }

        LOGGER.debug("Load properties: {}", props);
        return props;
    }
}
