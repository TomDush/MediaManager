package fr.dush.mediamanager.business.configuration;

import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.exceptions.PropertyUnresolvableException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Provide convenience methods to get property values and resolve variables in string.
 * <p/>
 * An <code>ModuleConfiguration</code> instance is created for each module to be injected. It wrap a FieldSet (list of
 * key-value from database or config files).
 *
 * @author Thomas Duchatelle
 */
public class ModuleConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfiguration.class);

    /** Pattern to detect values containing variables */
    private static Pattern pattern = Pattern.compile("\\$\\{([\\w\\._-]*)\\}");

    /** Configuration Manager is used to find value from other modules. */
    @Setter
    private IConfigurationManager configurationManager;

    /** Module ID, is also file name. */
    private String id;

    /** Wrapped field set (loaded from default value, database or config file */
    private FieldSet fieldSet;

    public ModuleConfiguration(String id, FieldSet fieldSet) {
        this.id = id;
        this.fieldSet = fieldSet;
    }

    /**
     * Get value for this property, resolve properties inside it (<code>${...}</code> template).
     *
     * @return Never null value
     *
     * @throws fr.dush.mediamanager.exceptions.PropertyUnresolvableException If property is not defined.
     * @see #readValue(String, Properties)
     */
    public String readValue(String key) {
        return readValue(key, new Properties());
    }

    /**
     * Get and resolve value for this property.
     *
     * @param defaultValue returned value if expected value is empty or default
     * @return Value found or defaultValue if no value has been defined.
     *
     * @see #readValue(String, Properties)
     */
    public String readValue(String key, String defaultValue) {
        Properties props = new Properties();
        props.put(key, defaultValue);
        props.put(id + "." + key, defaultValue);

        return readValue(key, props);
    }

    /**
     * Read property is this order : <ul> <li>Module specific value (value set in database, or property file)</li>
     * <li>Global generic value : defined for all application</li> <li>System properties</li> <li>Given default
     * properties</li> <li>Default module specific value</li> <li>Default generic value</li> </ul>
     */
    public String readValue(String key, Properties defaultProperties) {
        String value = getValue(key, defaultProperties);

        return resolveProperties(value, defaultProperties);
    }

    /** Get value as it is stored: may contains reference on other properties. */
    public String getValue(String key) {
        return getValue(key, new Properties());
    }

    /**
     * Get value as it is stored: may contains reference on other properties.
     *
     * @param key               Property key, can be prefixed by module name, (optional)
     * @param defaultProperties Properties to use instead of default (in file) or instead of null value.
     * @return Never null, but can be empty.
     */
    private String getValue(String key, Properties defaultProperties) {
        // Key can start by module name
        String relativeKey = key;
        boolean prefixed = false;
        if (isPrefixed(relativeKey)) {
            relativeKey = relativeKey.substring(id.length() + 1);
            prefixed = true;
        }

        // Try to read value in this fieldSet
        Field field = fieldSet.getFieldMap().get(relativeKey);
        String value = null;
        if (field != null) {
            value = field.getValue();

            // If value hasn't be defined (or is default), use value given in argument
            if (value == null || field.isDefaultValue()) {

                String otherDefault = defaultProperties.getProperty(prefixed ? key : id + "." + key);
                if (isNotBlank(otherDefault)) {
                    value = otherDefault;
                }
            }
        }

        // If not found in field set, try to find it in other modules (if key is compatible)
        if (value == null && !prefixed && key.contains(".")) {
            value = getValueFromOtherModules(key, defaultProperties);
        }

        // If value still null, try to find in system properties
        if (value == null && System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        }

        // If value is null, there are a configuration error.
        if (value == null) {
            throw new PropertyUnresolvableException(
                    "Property '%s' hasn't be defined. Even without value, it must be defined in JSON file.",
                    relativeKey);
        }

        return value;
    }

    private boolean isPrefixed(String relativeKey) {
        return relativeKey.startsWith(id + ".");
    }

    /**
     * Read value from other modules (by configurationManager)
     *
     * @return NULL if not found.
     */
    private String getValueFromOtherModules(String key, Properties defaultProperties) {
        try {
            return configurationManager.getModuleConfiguration(key.substring(0, key.indexOf(".")))
                                       .getValue(key, defaultProperties);

        } catch (ConfigurationException | NullPointerException e) {
            return null;
        }
    }

    /** Read value and cast it into integer. */
    public Integer readValueAsInt(String key) throws NumberFormatException {
        final String value = readValue(key);
        if (isEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value);
    }

    /** Read value and cast it to boolean. */
    public boolean readValueAsBoolean(String key) {
        final String value = readValue(key);
        if (isEmpty(value)) {
            return false;
        }

        return Boolean.valueOf(value);
    }

    /**
     * If pattern <code>${...}</code> is found, replace it by value found in generic properties, or system. This method
     * is recursive.
     *
     * @param value can be null or empty
     * @param props Default properties to use
     */
    public String resolveProperties(String value, Properties props) {
        if (isEmpty(value)) {
            return value;
        }

        String result = value;

        final Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            // Find corresponding value
            LOGGER.debug("Resolve property : {} (full var : {})", matcher.group(1), matcher.group());
            String subvalue = readValue(matcher.group(1), props);

            // Replace (recursive resolution...)
            if (isNotEmpty(subvalue)) {
                result = result.replace(matcher.group(), resolveProperties(subvalue, props));
            }

        }
        return result;
    }

    /** Add field, or override existing */
    public void addField(Field field) {
        if (fieldSet.getFieldMap().containsKey(field.getKey())) {
            fieldSet.getFieldMap().get(field.getKey()).merge(field);

        } else {
            simpleAdd(field);
        }

    }

    public void initialize(Collection<Field> fields) throws IOException {
        for (Field f : fields) {
            f.setDefaultValue(true);
            final Field existing = getField(f.getKey());

            if (null == existing || isEmpty(existing.getValue())) {
                // If no value corresponding, add new one as default.
                addField(f);

            } else {
                // Else, only update displayable data
                existing.updateDisplayable(f.getName(), f.getDescription());
            }

        }

    }

    /**
     * Return field, or null.
     */
    public Field getField(String key) {
        return fieldSet.getFieldMap().get(key);
    }

    /**
     * Return all fieldSet.getFieldMap().
     */
    public Collection<Field> getAllFields() {
        return fieldSet.getFieldMap().values();
    }

    public String getName() {
        return fieldSet.getName();
    }

    public String getPackageName() {
        return fieldSet.getConfigId();
    }

    /**
     * Override existing field if any...
     */
    protected void simpleAdd(Field field) {
        fieldSet.getFieldMap().put(field.getKey(), field);
    }

}
