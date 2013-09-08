package fr.dush.mediamanager.business.mediatech.impl;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.common.base.Function;

import fr.dush.mediamanager.business.mediatech.IRootDirectoryManager;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

@ApplicationScoped
public class RootDirectoryManagerImpl implements IRootDirectoryManager {

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	@Override
	public RootDirectory createOrUpdateRootDirectory(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException {

		convertPathsToAbsolute(rootDirectory);

		final List<RootDirectory> roots = rootDirectoryDAO.findUsingPath(rootDirectory.getPaths());

		if (roots.size() >= 2) {
			// KO : constrains exception.
			throw new RootDirectoryAlreadyExistsException("paths can't be shared between two or more RootDirectorys.", rootDirectory,
					getConflictPaths(rootDirectory, roots));

		} else if (roots.isEmpty() || containId(rootDirectory.getName(), roots)) {
			// New one, or existing one to update
			rootDirectoryDAO.saveOrUpdate(rootDirectory);

			return rootDirectoryDAO.findById(rootDirectory.getName());
		}

		// Try to merge new root to existing one.
		return mergeRootDirectory(rootDirectory, roots);
	}

	private RootDirectory mergeRootDirectory(RootDirectory rootDirectory, final List<RootDirectory> roots)
			throws RootDirectoryAlreadyExistsException {
		final RootDirectory root = findById(rootDirectory.getName());
		if (root != null) {
			// Already exist but paths are in conflict with another rootDirectory's paths
			throw new RootDirectoryAlreadyExistsException("paths can't be shared between two or more RootDirectorys.", rootDirectory,
					getConflictPaths(rootDirectory, roots));

		}

		final RootDirectory existing = roots.get(0);
		if (rootDirectory.getMediaType() != existing.getMediaType()) {
			throw new RootDirectoryAlreadyExistsException("can't update " + existing.getName() + " and change media type from "
					+ existing.getMediaType() + " to " + rootDirectory.getMediaType(), rootDirectory,
					getConflictPaths(rootDirectory, roots));

		}

		update(existing, rootDirectory);
		rootDirectoryDAO.saveOrUpdate(existing);

		return existing;
	}

	/**
	 * Update path, ...
	 *
	 * @param existing
	 * @param update
	 */
	private void update(RootDirectory existing, RootDirectory update) {
		// Properties
		existing.setEnricher(update.getEnricher());

		// Paths
		for (String p : update.getPaths()) {
			final Path newPath = Paths.get(p).toAbsolutePath();

			boolean notFound = true;
			final Iterator<String> it = existing.getPaths().iterator();

			while (notFound && it.hasNext()) {
				final Path existingPath = Paths.get(it.next()).toAbsolutePath();
				if (newPath.equals(existingPath) || newPath.startsWith(existingPath)) {
					// Same or subpath to existing one
					notFound = false;

				} else if (existingPath.startsWith(newPath)) {
					// Replace old path by new one
					it.remove();
				}

			}

			if (notFound) {
				existing.getPaths().add(newPath.toString());
			}
		}

	}

	private List<String> getConflictPaths(RootDirectory rootDirectory, List<RootDirectory> roots) {
		List<String> conflicts = newArrayList();
		for (final RootDirectory r : roots) {
			conflicts.addAll(transform(intersection(r.getPaths(), rootDirectory.getPaths()), new Function<String, String>() {
				@Override
				public String apply(String s) {
					return s + " shared with " + r.getName();
				}
			}));
		}

		return conflicts;
	}

	@Override
	public RootDirectory findById(String name) {
		return rootDirectoryDAO.findById(name);
	}

	@Override
	public List<RootDirectory> findAll() {
		return rootDirectoryDAO.findAll();
	}

	@Override
	public RootDirectory findBySubPath(Path path) {
		return rootDirectoryDAO.findBySubPath(path.toAbsolutePath());
	}

	@Override
	public void markAsUpdated(RootDirectory rootDirectory) {
		final Date now = new Date();
		rootDirectory.setLastRefresh(now);
		rootDirectoryDAO.markAsUpdated(rootDirectory.getName(), now);
	}

	/**
	 * Remove non-absolute paths, add them on their absolute form.
	 *
	 * @param rootDirectory
	 */
	private void convertPathsToAbsolute(RootDirectory rootDirectory) {
		List<String> absolutes = newArrayList();

		final Iterator<String> it = rootDirectory.getPaths().iterator();
		while (it.hasNext()) {
			absolutes.add(stringToAbsolutePath.apply(it.next()));
			it.remove();
		}

		rootDirectory.getPaths().addAll(absolutes);
	}

	private boolean containId(String name, List<RootDirectory> roots) {
		for (RootDirectory r : roots) {
			if (name.equals(r.getName())) {
				return true;
			}
		}
		return false;
	}

	private static Function<String, String> stringToAbsolutePath = new Function<String, String>() {
		@Override
		public String apply(String path) {
			return Paths.get(path).toAbsolutePath().normalize().toString();
		}
	};
}
