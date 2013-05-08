package fr.dush.mediamanager.business.configuration;

/**
 * Provide configuration on all application...
 *
 * <p>
 * Configuration values are found (first override next ones) :
 * <ol>
 * <li>Database persisted values</li>
 * <li>Configuration file</li>
 * <li>Default properties file in classpath</li>
 * <li>Values given in code</li>
 * </ol>
 * </p>
 *
 * @author Thomas Duchatelle
 *
 */
public interface IConfigurationManager {

	/**
	 * Get value specified by key param. If no configuration was found, return default. This default value is stored.
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String getValue(String key, String defaultValue);
}
