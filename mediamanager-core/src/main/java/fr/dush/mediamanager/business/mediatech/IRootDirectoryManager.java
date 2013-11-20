package fr.dush.mediamanager.business.mediatech;

import java.nio.file.Path;
import java.util.List;

import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

/**
 * Implement low level business rules on {@link RootDirectory}.
 *
 * <p>
 * Business rules are :
 * <ul>
 * <li>{@link RootDirectory#getPaths()} must be absolute</li>
 * <li><code>Paths</code> mustn't have duplicate, be include or include paths of others {@link RootDirectory}.
 * </ul>
 * </p>
 *
 * @author Thomas Duchatelle
 *
 */
public interface IRootDirectoryManager {

	/**
	 * Create or update rootDirectory, check conditions.
	 *
	 * @param rootDirectory
	 * @return Given rootDirectory if new.
	 * @throws RootDirectoryAlreadyExistsException
	 */
	RootDirectory createOrUpdateRootDirectory(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException;

	/**
	 * Find RootDirectory from one of its sub-path.
	 *
	 * @param path
	 * @return
	 */
	RootDirectory findBySubPath(Path path);

	List<RootDirectory> findAll();

	RootDirectory findById(String name);

	/**
	 * Set <code>lastRefresh</code> date to current date.
	 *
	 * @param rootDirectory
	 */
	void markAsUpdated(RootDirectory rootDirectory);
}
