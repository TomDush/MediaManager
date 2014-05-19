package fr.dush.mediamanager.dao.configuration.file;

import com.google.common.io.CharStreams;
import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Use property file to overload default configuration.
 * <p/>
 * <p> Path to file must be in <code>mediamanager.propertiesfile</code> system's properties. If it is defined, file must
 * exist. </p>
 *
 * @author Thomas Duchatelle
 */
@Named
public class FileConfigurationDAOImpl implements IConfigurationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigurationDAOImpl.class);

    private Path configFile = null;

    private Properties properties = new Properties();

    @PostConstruct
    public void initialize() {
        final String propertyFile = System.getProperty("mediamanager.propertiesfile");

        if (!isEmpty(propertyFile)) {
            configFile = Paths.get(propertyFile).normalize();

            if (!configFile.toFile().exists()) {
                throw new ConfigurationException("Properties file %s does't exist.", configFile);
            }

            try {
                // Do NOT interpret backslash \ in properties file.
                String propertiesContent =
                        CharStreams.toString(new InputStreamReader(new FileInputStream(configFile.toFile())));
                properties.load(new StringReader(propertiesContent.replace("\\", "\\\\")));
            } catch (IOException e) {
                throw new ConfigurationException("Properties file %s can't be read : %s", configFile, e.getMessage());
            }
        }

    }

    @Override
    public List<Field> findByPackage(String packageName) {
        List<Field> set = newArrayList();

        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String && ((String) entry.getKey()).startsWith(packageName + ".")) {
                final String key = (String) entry.getKey();
                LOGGER.debug("Read property for '{}' : {} = {}", packageName, key, entry.getValue());
                set.add(new Field(key.substring(packageName.length() + 1), (String) entry.getValue()));
            }
        }

        return set;
    }

    @Override
    public void save(FieldSet configuration) {
        throw new RuntimeException(
                "FileConfigurationDAOImpl.save isn't implemented : file value must be changed manually");
    }

}
