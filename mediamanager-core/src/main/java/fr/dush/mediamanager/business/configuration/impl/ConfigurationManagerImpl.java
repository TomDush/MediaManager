package fr.dush.mediamanager.business.configuration.impl;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;
import fr.dush.mediamanager.exceptions.ConfigurationException;

/**
 * This is NOT an alternative. It's this only alternative to force using producer.
 *
 * @author Thomas Duchatelle
 * @see IConfigurationManager
 *
 */
@ApplicationScoped
@Alternative
public class ConfigurationManagerImpl implements IConfigurationManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerImpl.class);

	@Inject
	private IConfigurationDAO configurationDAO;

	@Inject
	private ObjectMapper objectMapper;

	/** Configuration generic for all application : it doesn't depend on any module */
	private ModuleConfiguration generic;

	/**
	 * Instanciate generic module
	 *
	 * @throws IOException
	 */
	@PostConstruct
	public void generateGenericModule() {
		try {
			generic = getModuleConfiguration("generic", "Media Manager Configuration", "configuration/generic.json");
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage(), e);
		}
	}

	@Override
	public ModuleConfiguration getModuleConfiguration(String packageName, String configurationName, String definition) throws IOException {
		return getOrCreateConfiguration(new SimpleConfigurationArguments(packageName, configurationName, definition));
	}

	/**
	 * Produce appropriate {@link ModuleConfiguration} to class which need id, by injection.
	 *
	 * @param point
	 * @return
	 */
	@Produces
	public ModuleConfiguration findModuleConfiguration(InjectionPoint point) {
		LOGGER.debug("Create/find ModuleConfiguration for {}", point);

		final IConfigurationArguments wrapper = new ConfigurationInjectionPoint(point);

		try {
			// Find or create ModuleConfiguration
			return getOrCreateConfiguration(wrapper);

		} catch (IOException e) {
			throw new ConfigurationException(ConfigurationInjectionPoint.generateErrorString(point, e.getMessage()));
		}
	}

	/**
	 * Get or create configuration...
	 *
	 * @param wrapper
	 * @return
	 * @throws IOException
	 */
	protected ModuleConfiguration getOrCreateConfiguration(final IConfigurationArguments wrapper) throws IOException {
		final String packageName = wrapper.getPackage();
		FieldSet fieldSet = configurationDAO.findByPackage(packageName);
		if (null == fieldSet) {
			fieldSet = new FieldSet(packageName);
		}

		// Setting configuration name
		if (isEmpty(fieldSet.getName())) {
			fieldSet.setName(wrapper.getName());
		}

		// Initialize configuration...
		ModuleConfiguration config = new ModuleConfiguration(generic, fieldSet);
		final String file = wrapper.getDefinition();
		if (isNotBlank(file)) {
			LOGGER.info("Initialize {} with {}", fieldSet.getPackageName(), file);

			config.initialize(readClasspathFile(config, file));
		}

		// Save update and return...
		configurationDAO.save(fieldSet);

		return config;
	}

	/**
	 * Read and parse to {@link Field}s file in class path.
	 *
	 * @param config
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	protected List<Field> readClasspathFile(ModuleConfiguration config, final String file) throws IOException, JsonParseException,
			JsonMappingException {
		// Read file
		final InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
		if (null == stream) {
			throw new IOException(String.format("Can't load default configuration for '%s.%s' : class path file %s doesn't exist.",
					config.getPackageName(), config.getName(), file));
		}

		// Convert...
		final List<Field> fields = objectMapper.readValue(stream, new TypeReference<List<Field>>() {});
		return fields;
	}

	@Getter
	@AllArgsConstructor
	public class SimpleConfigurationArguments implements IConfigurationArguments {
		private String packageName;

		private String name;

		private String definition;

		@Override
		public String getPackage() {
			return getPackageName();
		}
	}

}
