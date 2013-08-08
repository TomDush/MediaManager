package fr.dush.mediamanager.dao.mediatech;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

/**
 * Provide access to persisted data on mediatech location (root directory), contains low level business rules.
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
public interface IRootDirectoryDAO {

	/**
	 * Find {@link RootDirectory} corresponding to path.
	 *
	 * @param path Path or subpath to directory...
	 * @return
	 */
	RootDirectory findBySubPath(Path path);

	/**
	 * Find {@link RootDirectory}s which have path containing or contained by this colleciton.
	 *
	 * @param paths
	 * @return
	 */
	List<RootDirectory> findUsingPath(Collection<String> paths);

	/**
	 * Save root directory if all its path aren't already used.
	 *
	 * @param rootDirectory
	 */
	void persist(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException;

	/**
	 * Update root directory, check it already exists and check new path aren't used.
	 *
	 * @param rootDirectory
	 * @throws RootDirectoryAlreadyExistsException
	 */
	void update(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException;

	/**
	 * Find all root directory, sorted by name.
	 *
	 * @return
	 */
	List<RootDirectory> findAll();
}
