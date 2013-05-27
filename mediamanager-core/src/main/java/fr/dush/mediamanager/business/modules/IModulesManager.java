package fr.dush.mediamanager.business.modules;

import java.util.Collection;

import fr.dush.mediamanager.exceptions.ModuleLoadingException;

/**
 * Manage modules : find modules which perform function.
 *
 * @author Thomas Duchatelle
 *
 */
public interface IModulesManager {

	/**
	 * Find modules (or beans) by type.
	 *
	 * @param moduleClass
	 * @return
	 */
	<T> Collection<T> findModuleByType(Class<? extends T> moduleClass);

	/**
	 * Get module by type and ID.
	 *
	 * @param moduleClass
	 * @param id
	 * @return
	 * @throws ModuleLoadingException
	 */
	<T> T findModuleById(Class<? extends T> moduleClass, String id) throws ModuleLoadingException;

}
