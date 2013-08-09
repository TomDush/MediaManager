package fr.dush.mediamanager.business.configuration.utils;

public interface IConfigurationArguments {

	/**
	 * Package's name : configuration's identifier (mandatory)
	 *
	 * @return package name, never NULL or empty.
	 */
	public String getPackage();

	/**
	 * Configuration's display name (or module name)
	 *
	 * @return Return can be null...
	 */
	public String getName();

	/**
	 * File's URL (in class path) containing properties descriptions and default value. Used to initialize configuration.
	 *
	 * @return can be null or empty.
	 */
	public String getDefinition();

}