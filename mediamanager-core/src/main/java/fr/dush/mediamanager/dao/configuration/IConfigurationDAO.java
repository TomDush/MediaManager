package fr.dush.mediamanager.dao.configuration;

import fr.dush.mediamanager.dto.configuration.FieldSet;

public interface IConfigurationDAO {

	/**
	 * Find persisted configuration by package name.
	 *
	 * @param packageName
	 * @return
	 */
	FieldSet findByPackage(String packageName);

	/**
	 * Save or update configuration...
	 * @param configuration
	 */
	void save(FieldSet configuration);
}
