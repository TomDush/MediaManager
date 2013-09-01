package fr.dush.mediamanager.business.configuration.utils;

import static org.apache.commons.lang3.StringUtils.*;

import javax.enterprise.inject.spi.InjectionPoint;

import lombok.Getter;
import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.exceptions.ConfigurationException;

/**
 * Provide utilities to get package name and module name from configuration injection point.
 *
 * @author Thomas Duchatelle
 *
 */
@Getter
public class ConfigurationInjectionPoint implements IConfigurationArguments {

	/** DO NOT USE : defined null value for {@link #entryPoint()}. */
	public static final Class<?> NULL_ENTRY_POINT = Configuration.class;

	/** Configuration injection point */
	private InjectionPoint point;

	/** Mandatory annotation {@link Configuration} on {@link #point} */
	private Configuration configurationAnnotation;

	/** Class declaring {@link Module} annotation, if any */
	private Class<?> moduleClass;

	/** Module annotation */
	private Module module;

	public ConfigurationInjectionPoint(InjectionPoint point) throws ConfigurationException {
		// Read injection point
		this.point = point;
		configurationAnnotation = point.getAnnotated().getAnnotation(Configuration.class);

		if (configurationAnnotation == null) {
			throw new ConfigurationException(generateErrorString(point,
					"To inject ModuleConfiguration, Configuration annotation is mandatory"));
		}

		// Read module (if any)
		if (NULL_ENTRY_POINT.equals(configurationAnnotation.entryPoint())) {
			// No entry point is defined, class may be directly Module...
			module = point.getBean().getBeanClass().getAnnotation(Module.class);
			if (module != null) {
				moduleClass = point.getBean().getBeanClass();
			}

		} else {
			// Module is defined, it must be valid (has Module annotation)
			moduleClass = configurationAnnotation.entryPoint();
			module = moduleClass.getAnnotation(Module.class);
			if (module == null) {
				throw new ConfigurationException(generateErrorString(point, "Class %s must be Module to be used as entryPoint.",
						configurationAnnotation.entryPoint().getName()));
			}
		}

	}

	@Override
	public String getPackage() {
		// package is overrided in configuration annotation
		if (isNotEmpty(configurationAnnotation.packageName())) {
			return configurationAnnotation.packageName();
		}

		// If module defined, get module package
		if (module != null) {
			if (isNotEmpty(module.packageName())) {
				return module.packageName();
			}

			return moduleClass.getPackage().getName();
		}

		// Otherwise, take package of injection point's class
		return point.getBean().getBeanClass().getPackage().getName();
	}

	@Override
	public String getName() {
		// Name is overrided in configuration annotation
		if (isNotEmpty(configurationAnnotation.name())) {
			return configurationAnnotation.name();
		}

		// If module defined, get module name
		if (null != module) {
			return module.name();
		}

		// Otherwise, no name...
		return null;
	}

	@Override
	public String getDefinition() {
		return configurationAnnotation.definition();
	}

	/**
	 * Generate full error message
	 *
	 * @param point
	 * @param error
	 * @param args
	 * @return
	 */
	public static String generateErrorString(InjectionPoint point, String error, Object... args) {
		final String errorLabel = String.format(error, args);

		return String.format("Error to inject configuration in %s.%s. %s", point.getBean().getBeanClass().getName(), point.getMember()
				.getName(), errorLabel);
	}

}
