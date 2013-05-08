package fr.dush.mediamanager.engine.mock;

import static com.google.common.collect.Maps.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Map;

import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.dto.configuration.FieldSet;

public class ConfigurationDAOMock implements IConfigurationDAO {

	private Map<String, FieldSet> map = newHashMap();

	@Override
	public FieldSet findByPackage(String packageName) {
		return map.get(packageName);
	}

	@Override
	public void save(FieldSet configuration) {
		if (isBlank(configuration.getPackageName())) {
			throw new IllegalArgumentException("ModuleConfiguration.packageName must not be null or blank.");
		}

		map.put(configuration.getPackageName(), configuration);
	}

}
