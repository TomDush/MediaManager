package fr.dush.mediamanager.modulesapi.lifecycle;

import fr.dush.mediamanager.exceptions.ModuleLoadingException;

import java.nio.file.Path;

/**
 * Service which be called before CDI context start.
 *
 * @author Thomas Duchatelle
 */
@Deprecated
public interface MediaManagerLifeCycleService {

    /** Called before <code>{@link fr.dush.mediamanager.tools.CDIUtils#bootCdiContainer()}</code> */
    void beforeStartCdi(Path configFilePath) throws ModuleLoadingException;

    /** Called after <code>{@link fr.dush.mediamanager.tools.CDIUtils#stopCdiContainer()}</code> */
    void afterStopCdi();
}
