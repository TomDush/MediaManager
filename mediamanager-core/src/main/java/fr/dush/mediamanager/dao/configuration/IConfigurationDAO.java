package fr.dush.mediamanager.dao.configuration;

import java.util.List;

import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;

public interface IConfigurationDAO {

	/**
	 * Find persisted configuration by package name.
	 *
	 * @param packageName
	 * @return
	 */
	List<Field> findByPackage(String packageName);

	/**
	 * Save or update configuration...
	 * @param configuration
	 */
	void save(FieldSet configuration);
}
