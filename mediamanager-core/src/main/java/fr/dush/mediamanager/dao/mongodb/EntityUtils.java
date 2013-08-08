package fr.dush.mediamanager.dao.mongodb;

import static org.apache.commons.lang3.StringUtils.*;

import com.google.code.morphia.annotations.Entity;

/**
 * Reflection tools on entities.
 *
 * @author Thomas Duchatelle
 *
 */
public class EntityUtils {

	public static String getCollectionName(final Class<?> clazz) {
		String collectionName = clazz.getSimpleName();

		// Find name in Entity annotation
		final Entity entityAnnotation = clazz.getAnnotation(Entity.class);
		if (null != entityAnnotation && isNotBlank(entityAnnotation.value()) && !".".equals(entityAnnotation.value())) {
			collectionName = entityAnnotation.value().trim();
		}

		// Use class name
		return collectionName;
	}
}
