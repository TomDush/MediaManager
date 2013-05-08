package fr.dush.mediamanager.business.configuration.impl;

public interface IConfigurationArguments {

	/**
	 * Get package name
	 *
	 * @return package name, never NULL or empty.
	 */
	public String getPackage();

	/**
	 * Get configuration name (or module name)
	 *
	 * @return Return can be null...
	 */
	public String getName();

	/**
	 * Get definition, if any.
	 *
	 * @return can be null or empty.
	 */
	public String getDefinition();

}