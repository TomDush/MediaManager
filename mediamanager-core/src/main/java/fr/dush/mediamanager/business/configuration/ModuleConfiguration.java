package fr.dush.mediamanager.business.configuration;

import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * An <code>ModuleConfiguration</code> instance is created for each module. It initialized by properties file with
 * default values, and it saved into database or properties file to persist changes.
 *
 * @author Thomas Duchatelle
 */
public class ModuleConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfiguration.class);

    /** Pattern to detect values containing variables */
    private static Pattern pattern = Pattern.compile("\\$\\{([\\w\\._-]*)\\}");

    /** If null, it's the generic ! */
    private final ModuleConfiguration generic;

    /** Wrapped field set */
    private FieldSet fieldSet;

    public ModuleConfiguration(ModuleConfiguration generic, FieldSet fieldSet) {
        this.generic = generic;
        this.fieldSet = fieldSet;
    }

    /**
     * Get value for this key. See {@link #readValue(String, Properties, boolean)}.
     *
     * @return Null if not defined.
     */
    public String readValue(String key) {
        return readValue(key, new Properties());
    }

    /**
     * Get value for this key, or default value. See {@link #readValue(String, Properties, boolean)}.
     *
     * @param defaultValue returned value if expected value is empty or default
     */
    public String readValue(String key, String defaultValue) {
        Properties props = new Properties();
        props.put(key, defaultValue);

        return readValue(key, props);
    }

    public String readValue(String key, Properties defaultProperties) {
        return readValue(key, defaultProperties, true);
    }

    /**
     * Read property is this order : <ul> <li>Module specific value (value set in database, or property file)</li>
     * <li>Global generic value : defined for all application</li> <li>System properties</li> <li>Given default
     * properties</li> <li>Default module specific value</li> <li>Default generic value</li> </ul>
     *
     * @param acceptDefault Accept default values (from property file, module or global)
     */
    public String readValue(String key, Properties defaultProperties, boolean acceptDefault) {
        String value = getValue(key, defaultProperties, acceptDefault);

        return resolveProperties(value, defaultProperties);
    }

    /** Get value but do not resolve it ! */
    public String getValue(String key) {
        return getValue(key, new Properties(), true);
    }

    /** Get value, don't resolve it */
    private String getValue(String key, Properties defaultProperties, boolean acceptDefault) {
        Field field = fieldSet.getFields().get(key);

        // Find value in this field set...
        String value = null;
        if (field != null) {
            value = field.getValue();
        }

        final boolean isDefault = field != null && field.isDefaultValue();
        if (isEmpty(value) || isDefault) {

            if (generic != null) {
                // Search in generic if value not found.
                value = defaultIfEmpty(generic.getValue(key, defaultProperties, !isDefault), value);

            } else {
                // It's the generic, find in System properties and default properties
                String globalValue = System.getProperty(key);

                if (isEmpty(globalValue)) {
                    globalValue = defaultProperties.getProperty(key);
                }

                if (isNotEmpty(globalValue)) {
                    value = globalValue;
                } else if (isDefault && !acceptDefault) {
                    value = null;
                }
            }
        }

        return value;
    }

    /**
     * Get value for this key
     *
     * @return Null if not defined.
     */
    public Integer readValueAsInt(String key) throws NumberFormatException {
        final String value = readDeepValue(key, null);
        if (isEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value);
    }

    public boolean readValueAsBoolean(String key) {
        final String value = readDeepValue(key, null);
        if (isEmpty(value)) {
            return false;
        }

        return Boolean.valueOf(value);
    }

    /**
     * Resolve value against all available properties (generic, value, ...).
     */
    public String readDeepValue(String key, Properties props) {
        String value = resolveProperties("${" + key + "}", props == null ? new Properties() : props);

        if (isEmpty(value) || value.matches("^\\$\\{.*\\}$")) {
            return null;
        }

        return value;
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

    /**
     * Add field, or override existing
     */
    public void addField(Field field) {
        if (fieldSet.getFields().containsKey(field.getKey())) {
            fieldSet.getFields().get(field.getKey()).merge(field);

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
        return fieldSet.getFields().get(key);
    }

    /**
     * Return all fieldSet.getFields().
     */
    public Collection<Field> getAllFields() {
        return fieldSet.getFields().values();
    }

    public String getName() {
        return fieldSet.getName();
    }

    public String getPackageName() {
        return fieldSet.getPackageName();
    }

    /**
     * Override existing field if any...
     */
    protected void simpleAdd(Field field) {
        fieldSet.getFields().put(field.getKey(), field);
    }

}
