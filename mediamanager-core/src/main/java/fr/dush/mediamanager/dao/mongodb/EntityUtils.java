package fr.dush.mediamanager.dao.mongodb;

import fr.dush.mediamanager.annotations.mapping.DBCollection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection tools on entities.
 *
 * @author Thomas Duchatelle
 */
public class EntityUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUtils.class);

    /** Get collection of given domain class. If annotation DBCollection isn't defined, take class name. */
    public static String getCollectionName(Class<?> clazz) {
        DBCollection collection = clazz.getAnnotation(DBCollection.class);
        if (collection != null && StringUtils.isNotBlank(collection.value())) {
            LOGGER.debug("Use collection name {} for class : {}", collection.value(), clazz.getName());
            return collection.value();
        }

        LOGGER.debug("Use class name as collection name : {}", clazz.getName());
        return clazz.getSimpleName();
    }
}
