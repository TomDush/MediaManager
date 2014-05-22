package fr.dush.mediamanager.business.configuration;

import java.util.Collection;

/**
 * @author Thomas Duchatelle
 */
public interface IConfigurationManager {

    /**
     * Get configuration for module with given id.
     *
     * @param id Id is also name of file.
     * @return Module configuration wrapping asked configuration.
     */
    ModuleConfiguration getModuleConfiguration(String id);

    /** Get all registered configuration. */
    Collection<fr.dush.mediamanager.domain.configuration.FieldSet> getAllConfigurations();
}
