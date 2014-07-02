package fr.dush.mediamanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import fr.dush.mediamanager.business.configuration.impl.ConfigurationManagerImpl;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Spring Java Based Configuration for Medima application. Provide a property place holder based on application
 * properties file (defined in Spring environment) and default packaged properties (in JSON files).
 */
@Configuration
@ComponentScan(basePackages = "fr.dush.mediamanager")
public class SpringConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);

    private static SpringPropertyPlaceholderConfigurer placeholderConfigurer;

    /**
     * Configure a place holder based on application.properties file, system and envrionment variables and some JSON
     * default variables.
     */
    @Bean
    public static PropertyPlaceholderConfigurer placeholder(ApplicationContext applicationContext) throws IOException {

        placeholderConfigurer = new SpringPropertyPlaceholderConfigurer();

        // Let Spring placeholder read property file for us (it has first priority)
        if (applicationContext.getEnvironment().containsProperty("mediamanager.propertiesfile")) {
            String propertyFile = applicationContext.getEnvironment()
                                                    .resolveRequiredPlaceholders("file:${mediamanager.propertiesfile}");
            placeholderConfigurer.setLocations(new Resource[]{applicationContext.getResource(propertyFile)});
        }

        // Then, load required properties from default configuration (JSON files)
        placeholderConfigurer.setProperties(loadPropertiesFromJson(applicationContext, "mongodb"/*, "remotecontrol"*/));

        return placeholderConfigurer;

    }

    /** Load properties value from JSON files. */
    private static Properties loadPropertiesFromJson(ApplicationContext applicationContext,
                                                     String... files) throws IOException {
        Properties props = new Properties();

        props.setProperty("staticFiles", Joiner.on(",").join(files));

        for (String file : files) {
            ObjectMapper mapper = new ObjectMapper();

            FieldSet fieldSet = ConfigurationManagerImpl.readFieldSetFile(applicationContext, mapper, file);

            for (Field field : fieldSet.getFields()) {
                // Fields are set with absolute path
                props.put(file + "." + field.getKey(), field.getValue());
            }
        }

        LOGGER.debug("Load properties: {}", props);
        return props;
    }

    /**
     * Implementation to keep properties loaded by placeholder.
     */
    public static class SpringPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

        @Getter
        private Properties properties = new Properties();

        private int springSystemPropertiesMode;

        @Override
        public void setSystemPropertiesMode(int systemPropertiesMode) {
            super.setSystemPropertiesMode(systemPropertiesMode);
            springSystemPropertiesMode = systemPropertiesMode;
        }

        @Override
        protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                         Properties props) throws BeansException {
            super.processProperties(beanFactoryToProcess, props);

            for (Object key : props.keySet()) {
                properties.put(key, resolvePlaceholder(key.toString(), props, springSystemPropertiesMode));
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Properties loaded:");
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    LOGGER.debug("\t- {}: {}", entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
