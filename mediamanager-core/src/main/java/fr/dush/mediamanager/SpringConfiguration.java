package fr.dush.mediamanager;

import com.google.common.base.Splitter;
import fr.dush.mediamanager.business.configuration.SimpleConfiguration;
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
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Spring Java Based Configuration for Medima application. Provide a property place holder based on application
 * properties file (defined in Spring environment) and default packaged properties (in JSON files).
 */
@Configuration
@ComponentScan(basePackages = "fr.dush.mediamanager")
public class SpringConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);

    private static final String STATIC_FILES_PROP = "staticFiles";
    public static final String PORT_PROP = "remotecontrol.port";

    /**
     * Placeholder is used for static configuration: config which is needed to start application and which can not be
     * override in database.
     * <p/>
     * It based on application properties, system and env variables, and some internal JSON files.
     */
    @Bean
    @Inject
    public static PropertyPlaceholderConfigurer placeholder(@Named(
            "staticConfiguration") Properties staticConfiguration) throws IOException {

        // Configure placeholder with this static configuration
        SpringPropertyPlaceholderConfigurer placeholderConfigurer = new SpringPropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(staticConfiguration);

        return placeholderConfigurer;
    }

    /**
     * This static configuration is based on some JSON files, then application properties and values given as arguments
     */
    @Bean(name = "staticConfiguration")
    private static Properties readStaticConfiguration(ApplicationContext applicationContext) {
        // Read static configuration
        Environment env = applicationContext.getEnvironment();

        String propertyFile = env.getRequiredProperty("mediamanager.propertiesfile");
        LOGGER.debug("Populate static configuration from value: propertiesfile={} ; staticFiles={} ; port={}",
                     propertyFile,
                     env.getProperty(STATIC_FILES_PROP),
                     env.getProperty(PORT_PROP));

        List<String> staticFiles = newArrayList("mongodb");
        staticFiles.addAll(getStaticFiles(applicationContext));

        SimpleConfiguration config = new SimpleConfiguration(Paths.get(propertyFile),
                                                             readPort(env),
                                                             staticFiles.toArray(new String[staticFiles.size()]));
        return config.getProperties();
    }

    /** Read port in env, return NULL if not set. */
    private static Integer readPort(Environment env) {
        String value = env.getProperty(PORT_PROP);
        return isNumeric(value) ? Integer.valueOf(value) : null;
    }

    /** Get name of JSON files to be loaded statically */
    private static ArrayList<String> getStaticFiles(ApplicationContext applicationContext) {
        String fileIds = applicationContext.getEnvironment().getProperty(STATIC_FILES_PROP);
        if (isNotEmpty(fileIds)) {
            return newArrayList(Splitter.on(",").split(fileIds));
        } else {
            return new ArrayList<>();
        }
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
