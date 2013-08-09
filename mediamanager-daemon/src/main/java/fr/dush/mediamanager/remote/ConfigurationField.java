package fr.dush.mediamanager.remote;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class ConfigurationField implements Serializable, Comparable<ConfigurationField> {

	/** Package + name */
	private String fullname;

	/** Resolved value */
	private String value;

	private boolean defaultValue = false;

	private String description;

	@Override
	public int compareTo(ConfigurationField arg0) {
		return fullname.compareTo(arg0.getFullname());
	}
}
