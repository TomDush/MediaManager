package fr.dush.mediamanager.dao.mediatech;


import java.nio.file.Path;

import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExists;

/**
 * Provide access to persisted data on mediatech location (root directory).
 *
 * @author Thomas Duchatelle
 *
 */
public interface IRootDirectoryDAO {

	/**
	 * Find {@link RootDirectory} corresponding to path.
	 *
	 * @param path Path to directory, subpath or parent path.
	 * @return
	 */
	RootDirectory findRootDirectory(Path path);

	/**
	 * Save root directory if all its path aren't already defined.
	 * @param rootDirectory
	 */
	void save(RootDirectory rootDirectory) throws RootDirectoryAlreadyExists;
}
