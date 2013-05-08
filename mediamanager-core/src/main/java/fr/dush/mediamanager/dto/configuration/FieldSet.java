package fr.dush.mediamanager.dto.configuration;

import static com.google.common.collect.Maps.*;

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

}
