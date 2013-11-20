package fr.dush.mediamanager.engine.mock;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Alternative;

import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;

@Alternative
public class ConfigurationDAOMock implements IConfigurationDAO {

	private Map<String, FieldSet> map = newHashMap();

	@Override
	public List<Field> findByPackage(String packageName) {
		if(map.containsKey(packageName)) {
			return newArrayList(map.get(packageName).getFields().values());
		}

		return newArrayList();
	}

	@Override
	public void save(FieldSet configuration) {
		if (isBlank(configuration.getPackageName())) {
			throw new IllegalArgumentException("ModuleConfiguration.packageName must not be null or blank.");
		}

		map.put(configuration.getPackageName(), configuration);
	}

}
