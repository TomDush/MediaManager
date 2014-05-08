package fr.dush.mediamanager.dao.mediatech.mongodb;

import com.google.common.collect.Lists;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * Using MongoDB database, and implementing low level rules as describe in {@link IRootDirectoryDAO}.
 *
 * @author Thomas Duchatelle
 */
public class RootDirectoryDAOImpl extends AbstractDAO<RootDirectory, String> implements IRootDirectoryDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectoryDAOImpl.class);

    @Inject
    public RootDirectoryDAOImpl() {
        super(RootDirectory.class);
    }

    @Override
    public RootDirectory saveOrUpdate(RootDirectory rootDirectory) {
        save(rootDirectory);

        return rootDirectory;
    }

    @Override
    public void markAsUpdated(String rootDirectoryName, Date updatedDate) {
        getCollection().update("{_id : #}", rootDirectoryName).with("{$set : {lastRefresh : #} }", updatedDate);
    }

    @Override
    public RootDirectory findBySubPath(String path) {
        final List<RootDirectory> roots = findUsingPath(newArrayList(path));

        if (1 == roots.size()) {
            return roots.get(0);
        } else if (roots.isEmpty()) {
            return null;
        }

        throw new IllegalStateException(
                "Saved RootDirectories are in illegal state : found multiple root for one path : " + path);
    }

    @Override
    public List<RootDirectory> findUsingPath(Collection<String> paths) {
        String masterQuery = "{ $or : [ %s { $where : \"%s\" } ] }";

        StringBuilder js = new StringBuilder();
        StringBuilder regexp = new StringBuilder();
        js.append("function() { \n\tvar found = false; \n\tthis.paths.forEach(function(p) { \n");

        for (String p : paths) {
            String path = p.replaceAll("\\\\", "\\\\\\\\");
            regexp.append("{ paths : { $regex : '").append(path).append("' }}, ");
            js.append("\t\tfound |= '").append(path).append("'.indexOf(p) == 0;\n");
        }

        js.append("\t});\n\treturn found; \n}");

        LOGGER.debug("Execute query with where =\n{}\n and regexps : {}", js, regexp);
        return Lists.newArrayList(getCollection().find(String.format(masterQuery, regexp, js)).as(RootDirectory.class));
    }
}
