package fr.dush.mediamanager.dao.mediatech.mongodb;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Lists.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Criteria;
import com.google.code.morphia.query.CriteriaContainer;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.WhereCriteria;
import com.google.common.base.Function;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

/**
 * Using MongoDB database, and implementing low level rules as describe in {@link IRootDirectoryDAO}.
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
public class RootDirectoryDAOImpl extends BasicDAO<RootDirectory, String> implements IRootDirectoryDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectoryDAOImpl.class);

	@Inject
	public RootDirectoryDAOImpl(Datastore datastore) {
		super(RootDirectory.class, datastore);
	}

	@Override
	public List<RootDirectory> findAll() {
		return createQuery().asList();
	}

	@Override
	public void persist(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException {
		toAbsolutePath(rootDirectory);
		// Check...
		final List<String> ids = findIds("_id", rootDirectory.getName());
		if (!ids.isEmpty()) {
			LOGGER.warn("Trying to persist existing root directory : {}. Redirect to update method...", rootDirectory);
			update(rootDirectory);
		}

		final List<RootDirectory> roots = findUsingPath(rootDirectory.getPaths());

		if (roots.isEmpty()) {
			// Saving
			save(rootDirectory);

		} else {
			// TODO List matching path...
			throw new RootDirectoryAlreadyExistsException("Paths can't be share between RootDirectory.", rootDirectory,
					new ArrayList<String>());
		}

	}

	@Override
	public void update(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException {
		final List<RootDirectory> roots = findUsingPath(rootDirectory.getPaths());
		if (roots.size() >= 2) {
			// At least 1 is bad : sharing same path/subpath.
			throw new RootDirectoryAlreadyExistsException("Paths can't be shared between two or more RootDirectorys.", rootDirectory,
					new ArrayList<String>());
		}

		if (!roots.isEmpty() && !roots.get(0).getName().equals(rootDirectory.getName())) {
			// If isn't itself, it can be renamed, or shared path/subpath.
			throw new RootDirectoryAlreadyExistsException("You can't use update method to rename RootDirectory. Use delete, then save.",
					rootDirectory, new ArrayList<String>());
		}

		// Updating ...
		save(rootDirectory);
	}

	@Override
	public RootDirectory findBySubPath(Path path) {
		final List<RootDirectory> roots = findUsingPath(newArrayList(path.toFile().getAbsolutePath()));

		if (1 == roots.size()) return roots.get(0);
		else if (roots.isEmpty()) return null;

		throw new IllegalStateException("Saved RootDirectories are in illegal state : found multiple root for one path : " + path);
	}

	@Override
	public List<RootDirectory> findUsingPath(Collection<String> paths) {
		final Query<RootDirectory> query = createQuery();

		final CriteriaContainer or = query.or(new Criteria[0]);

		StringBuffer js = new StringBuffer();
		js.append("function() { \n\tvar found = false; \n\tthis.paths.forEach(function(p) { \n");

		for (String p : transform(paths, stringToAbsolutePath)) {
			or.add(query.criteria("paths").contains(p));
			js.append("\t\tfound |= '").append(p).append("'.indexOf(p) == 0;\n");
		}

		js.append("\t});\n\treturn found; \n}");

		or.add(new WhereCriteria(js.toString()));

		LOGGER.debug("Execute query with where =\n{}", js);

		return query.asList();
	}

	private Function<String, String> stringToAbsolutePath = new Function<String, String>() {
		@Override
		public String apply(String path) {
			return Paths.get(path).toAbsolutePath().toString();
		}
	};

	/**
	 * Remove non-absolute paths, add them on their absolute form.
	 *
	 * @param rootDirectory
	 */
	private void toAbsolutePath(RootDirectory rootDirectory) {
		List<String> absolutes = newArrayList();

		final Iterator<String> it = rootDirectory.getPaths().iterator();
		while (it.hasNext()) {
			Path p = Paths.get(it.next());
			if (!p.isAbsolute()) {
				it.remove();
				absolutes.add(p.toFile().getAbsolutePath());
			}
		}

		rootDirectory.getPaths().addAll(absolutes);
	}
}
