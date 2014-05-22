package fr.dush.mediamanager.business.configuration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Provide configuration on all application, implementation must be a {@link ModuleConfiguration} producer !
 * <p/>
 * Configuration values are found (first override next ones) : <ol> <li>Database persisted values</li>
 * <li>(Configuration file)</li> <li>Default properties file in classpath</li> <li>Values given in code</li> </ol>
 *
 * @author Thomas Duchatelle
 * @see fr.dush.mediamanager.business.configuration.producers.ConfigurationBeanPostProcessor
 */
@Service
public class ConfigurationManagerImpl implements IConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerImpl.class);

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private IConfigurationDAO configurationDAO;
    @Inject
    private ObjectMapper objectMapper;

    /** JSON default config file used to initialised Spring placeholder */
    @Value("${staticFiles}")
    private Set<String> staticFiles;

    /** Local cache of all loaded field sets */
    private Map<String, FieldSet> fieldSets = new HashMap<>();

    /**
     * Load JSON file which has been used by placeholder - they can't be changed by software.
     */
    @PostConstruct
    public void loadStaticConfiguration() throws IOException {
        for (String f : staticFiles) {
            FieldSet fieldSet = getFieldSet(f);
            // Mark all its fields as static - means could not be changed without editing manually properties file
            for (Field field : fieldSet.getFields()) {
                field.setStaticField(true);
            }
        }
    }

    /**
     * Read field set from JSON file and override it with environment variables
     *
     * @param mapper   JSON Mapper to deserialise file
     * @param fileName File name, without repertory or extension.
     */
    public static FieldSet readFieldSetFile(ApplicationContext applicationContext, ObjectMapper mapper,
                                            String fileName) {

        String filePath = "configuration/" + fileName + ".json";

        try {
            Resource resource = applicationContext.getResource(filePath);
            FieldSet fieldSet = mapper.readValue(resource.getInputStream(), FieldSet.class);
            overrideWithEnvironmentVariables(fieldSet);

            return fieldSet;
        } catch (IOException e) {
            throw new ConfigurationException("Configuration file %s doesn't exist in classpath.", filePath);
        }
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

    @Override
    public ModuleConfiguration getModuleConfiguration(String id) {
        return new ModuleConfiguration(id, getFieldSet(id));
    }

    @Override
    public Collection<FieldSet> getAllConfigurations() {
        return Collections.unmodifiableCollection(fieldSets.values());
    }

    /**
     * Get already loaded field set, or read one (from file and database).
     *
     * @param id File name (without extension)
     */
    protected FieldSet getFieldSet(String id) {
        FieldSet fieldSet = fieldSets.get(id);

        if (fieldSet == null) {
            // Load default value and displayable data (meta data)...
            fieldSet = readFieldSetFile(applicationContext, objectMapper, id);

            // Override with user preference (saved in database)
            List<Field> fields = configurationDAO.findByPackage(id);
            for (Field f : fields) {
                fieldSet.addValue(f.getKey(), f.getValue(), false);
            }

            fieldSets.put(id, fieldSet);
            LOGGER.info("Created FieldSet: {}", fieldSet);
        }

        return fieldSet;
    }

}
