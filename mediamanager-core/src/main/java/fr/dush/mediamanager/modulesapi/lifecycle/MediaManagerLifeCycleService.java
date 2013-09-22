package fr.dush.mediamanager.modulesapi.lifecycle;

import java.nio.file.Path;

import fr.dush.mediamanager.exceptions.ModuleLoadingException;

/**
 * Service which be called before CDI context start.
 *
 * @author Thomas Duchatelle
 *
 */
public interface MediaManagerLifeCycleService {

	/** Called before <code>{@link fr.dush.mediamanager.tools.CDIUtils#bootCdiContainer()}</code> */
	void beforeStartCdi(Path configFilePath) throws ModuleLoadingException;

	/** Called after <code>{@link fr.dush.mediamanager.tools.CDIUtils#stopCdiContainer()}</code> */
	void afterStopCdi();
}
