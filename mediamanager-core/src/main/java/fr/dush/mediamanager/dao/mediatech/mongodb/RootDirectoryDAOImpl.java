package fr.dush.mediamanager.dao.mediatech.mongodb;

import static com.google.common.collect.Lists.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.query.Criteria;
import com.google.code.morphia.query.CriteriaContainer;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.WhereCriteria;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;

/**
 * Using MongoDB database, and implementing low level rules as describe in {@link IRootDirectoryDAO}.
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
public class RootDirectoryDAOImpl extends AbstractDAO<RootDirectory, String> implements IRootDirectoryDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectoryDAOImpl.class);

	@Inject
	public RootDirectoryDAOImpl() {
		super(RootDirectory.class);
	}

	@Override
	public List<RootDirectory> findAll() {
		return createQuery().asList();
	}

	@Override
	public RootDirectory saveOrUpdate(RootDirectory rootDirectory) {
		save(rootDirectory);

		return findById(rootDirectory.getName());
	}

	@Override
	public void markAsUpdated(String rootDirectoryName, Date updatedDate) {
		final Query<RootDirectory> query = createQuery();
		query.and(query.criteria("_id").equal(rootDirectoryName));

		getDs().update(query, getDs().createUpdateOperations(RootDirectory.class).set("lastRefresh", updatedDate));
	}

	@Override
	public RootDirectory findBySubPath(Path path) {
		final List<RootDirectory> roots = findUsingPath(newArrayList(path.toFile().getAbsolutePath()));

		if (1 == roots.size()) {
			return roots.get(0);
		} else if (roots.isEmpty()) {
			return null;
		}

		throw new IllegalStateException("Saved RootDirectories are in illegal state : found multiple root for one path : " + path);
	}

	@Override
	public List<RootDirectory> findUsingPath(Collection<String> paths) {
		final Query<RootDirectory> query = createQuery();

		final CriteriaContainer or = query.or(new Criteria[0]);

		StringBuffer js = new StringBuffer();
		js.append("function() { \n\tvar found = false; \n\tthis.paths.forEach(function(p) { \n");

		for (String p : paths) {
			or.add(query.criteria("paths").contains(p));
			js.append("\t\tfound |= '").append(p).append("'.indexOf(p) == 0;\n");
		}

		js.append("\t});\n\treturn found; \n}");

		or.add(new WhereCriteria(js.toString()));

		LOGGER.debug("Execute query with where =\n{}", js);

		return query.asList();
	}
}
