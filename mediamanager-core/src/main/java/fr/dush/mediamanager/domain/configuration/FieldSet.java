package fr.dush.mediamanager.domain.configuration;

import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Simple data object which group properties of each module.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
@EqualsAndHashCode(of = "packageName")
@NoArgsConstructor
public class FieldSet {

	/** Package name (configuration ID) */
	private String packageName;

	/** Module name */
	private String name;

	/** Configuration's values key => value */
	private Map<String, Field> fields = newHashMap();

	public FieldSet(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Add or Set value
	 *
	 * @param key
	 * @param value
	 * @param defaultValue if this value is default value...
	 */
	public void addValue(String key, String value, boolean defaultValue) {
		if (fields.containsKey(key)) {
			final Field f = fields.get(key);
			f.setValue(value);
			f.setDefaultValue(defaultValue);

		} else {
			fields.put(key, new Field(key, value));
		}
	}

	public void addAllFields(List<Field> fields, boolean b) {
		for (Field f : fields) {
			addField(f);
			f.setDefaultValue(b);
		}
	}

	/**
	 * Add field, replace existing one if any.
	 *
	 * @param f
	 */
	private void addField(Field f) {
		fields.put(f.getKey(), f);
	}

	@Override
	public String toString() {
		final Map<String, String> map = newHashMap();
		for (Field f : fields.values()) {
			map.put(f.getKey(), f.getValue());
		}

		return "FieldSet [packageName=" + packageName + ", name=" + name + ", fields=" + map + "]";
	}

}
