package fr.dush.mediamanager.business.configuration;

import java.io.IOException;


/**
 * Provide configuration on all application, implementation must be a {@link ModuleConfiguration} producer !
 *
 * <p>
 * Configuration values are found (first override next ones) :
 * <ol>
 * <li>Database persisted values</li>
 * <li>(Configuration file)</li>
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
	 * Get persisted configuration, or create and initialize new one.
	 *
	 * @param packageName Package's name : configuration's identifier (mandatory)
	 * @param configurationName Configuration's display name
	 * @param description URL in class path of configuration's definition (to initialize configuration)
	 * @return Corresponding ModuleConfiguration, never null.
	 * @throws IOException Raised if definition file doesn't exist in classpath.
	 */
	ModuleConfiguration getModuleConfiguration(String packageName, String configurationName, String definition) throws IOException;
}
