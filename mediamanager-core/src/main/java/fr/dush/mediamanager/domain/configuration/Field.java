package fr.dush.mediamanager.domain.configuration;

import static org.apache.commons.lang3.StringUtils.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration field
 *
 * @author Thomas Duchatelle
 *
 */
@Data
@NoArgsConstructor
public class Field {

	private String key;

	private String value;

	private String name;

	private String description;

	private boolean defaultValue = false;

	/** Create field with NON-default value */
	public Field(String key, String value) {
		this(key, value, false);
	}

	public Field(String key, String value, boolean defaultValue) {
		this.key = key;
		this.value = value;
		this.defaultValue = defaultValue;
	}

	/**
	 * Update this object with other field. Override key and value. Other attribute are updated is there are not empty.
	 *
	 * @param field
	 */
	public void merge(Field field) {
		key = field.key;
		value = field.value;
		defaultValue = field.defaultValue;

		updateDisplayable(field.name, field.description);
	}

	public void updateDisplayable(String newName, String newDescription) {
		if (isNotEmpty(newName)) name = newName;
		if (isNotEmpty(newDescription)) description = newDescription;
	}

	public void setValue(String value) {
		this.value = value;
		this.defaultValue = false;
	}
}
