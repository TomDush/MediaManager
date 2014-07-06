package fr.dush.mediamanager.business.configuration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.configuration.SimpleConfiguration;
import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

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
    private IConfigurationDAO configurationDAO;
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    @Named("staticConfiguration")
    private Properties staticConfiguration;

    /** JSON default config file used to initialised Spring placeholder */
    @Value("${staticFiles}")
    private String staticFiles;

    /** Local cache of all loaded field sets */
    private Map<String, FieldSet> fieldSets = new HashMap<>();

    /**
     * Load JSON file which has been used by placeholder - they can't be changed by software.
     */
    @PostConstruct
    public void loadStaticConfiguration() throws IOException {
        LOGGER.debug("Static properties: {}", staticConfiguration);

        // Fix properties statically loaded with final values, and mark as static.
        for (String f : Splitter.on(",").split(staticFiles)) {
            FieldSet fieldSet = getFieldSet(f);
            // Mark all its fields as static - means could not be changed without editing manually properties file
            for (Field field : fieldSet.getFields()) {
                field.setValue(staticConfiguration.getProperty(fieldSet.getConfigId() + "." + field.getKey()));
                field.setStaticField(true);
            }
        }
    }

    @Override
    public ModuleConfiguration getModuleConfiguration(String id) {
        return new ModuleConfiguration(this, id, getFieldSet(id));
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
            fieldSet = SimpleConfiguration.readFieldSetFile(objectMapper, id);

            // Override with user preference (saved in database) and placeholder properties
            List<Field> fields = configurationDAO.findByPackage(id);
            for (Field f : fields) {
                fieldSet.addValue(f.getKey(), f.getValue(), false);
            }

            // Override by properties placeholder
            for (Field field : fieldSet.getFields()) {
                if (staticConfiguration.containsKey(field.getKey())) {
                    Object value = staticConfiguration.get(field.getKey());
                    field.setValue(value == null ? null : value.toString());

                    field.setStaticField(true);
                }
            }

            fieldSets.put(id, fieldSet);
            LOGGER.info("Created FieldSet: {}", fieldSet);
        }

        return fieldSet;
    }

}
