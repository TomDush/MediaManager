package fr.dush.mediamanager.business.configuration;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;

/**
 * An <code>ModuleConfiguration</code> instance is created for each module. It initialized by properties file with default values, and it
 * saved into database or properties file to persist changes.
 *
 * @author Thomas Duchatelle
 *
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
	 * Get value from generic
	 *
	 * @param key
	 * @return Null if not defined.
	 */
	public String readGenericValue(String key) {
		if (null == generic) {
			// Generic is itself !
			return readValue(key);
		}

		return generic.readValue(key);
	}

	/**
	 * Get value for this key
	 *
	 * @param key
	 * @return Null if not defined.
	 */
	public String readValue(String key) {
		if (fieldSet.getFields().containsKey(key)) {
			return resolveGenericProperties(fieldSet.getFields().get(key).getValue());
		}

		return null;
	}

	/**
	 * Get value for this key
	 *
	 * @param key
	 * @return Null if not defined.
	 */
	public Integer readValueAsInt(String key) throws NumberFormatException {
		final String value = readValue(key);
		if (null == value) return null;

		return Integer.valueOf(value);
	}

	/**
	 * If pattern <code>${...}</code> is found, replace it by value found in generic properties, or system. This method is recursive.
	 *
	 * @param value
	 * @return
	 */
	protected String resolveGenericProperties(String value) {
		final Matcher matcher = pattern.matcher(value);
		while (matcher.find()) {
			// Find corresponding value
			LOGGER.debug("Resolve property : {} (full var : {})", matcher.group(1), matcher.group());
			String prop = readValue(matcher.group(1));

			// Search in generic if value not found.
			if (isEmpty(prop) && null != generic) {
				prop = generic.readValue(matcher.group(1));
			}

			// Else, search in System
			if (isEmpty(prop)) {
				prop = System.getProperty(matcher.group(1));
			}

			// Replace (recursive resolution...)
			if (isNotEmpty(prop)) {
				value = value.replace(matcher.group(), resolveGenericProperties(prop));
			}

		}
		return value;
	}

	/**
	 * Get value for this key, return defaultValue if it's null, empty or default. Given default value will not be saved.
	 *
	 * @param key
	 * @param defaultValue returned value if expected value is empty or default
	 * @return
	 */
	public String readValue(String key, String defaultValue) {
		Field field = fieldSet.getFields().get(key);
		if (null == field) {
			// If field wasn't known, create field with default value.
			field = new Field(key, defaultValue);
			field.setDefaultValue(true);
			simpleAdd(field);

			return resolveGenericProperties(defaultValue);
		}

		// If no value or default, return given default
		if (isEmpty(field.getValue()) || field.isDefaultValue()) {
			return defaultValue;
		}

		return resolveGenericProperties(field.getValue());
	}

	/**
	 * Add field, or override existing
	 *
	 * @param field
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
	 *
	 * @param key
	 * @return
	 */
	public Field getField(String key) {
		return fieldSet.getFields().get(key);
	}

	/**
	 * Return all fieldSet.getFields().
	 *
	 * @return
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
	 *
	 * @param field
	 */
	protected void simpleAdd(Field field) {
		fieldSet.getFields().put(field.getKey(), field);
	}

}
