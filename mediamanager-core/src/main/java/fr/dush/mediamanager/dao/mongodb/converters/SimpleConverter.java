package fr.dush.mediamanager.dao.mongodb.converters;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.converters.TypeConverter;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import fr.dush.mediamanager.dao.mongodb.EntityUtils;

/**
 * Simple converter using reflection (not finished)...
 *
 * @author Thomas Duchatelle
 *
 */
public class SimpleConverter extends TypeConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConverter.class);

	private static final Set<Class<?>> primitives = newHashSet();

	private Map<String, EntityMetaData> metaDatas = newHashMap();

	static {
		primitives.add(Boolean.class);
		primitives.add(Byte.class);
		primitives.add(Character.class);
		primitives.add(Short.class);
		primitives.add(Integer.class);
		primitives.add(Long.class);
		primitives.add(Float.class);
		primitives.add(Double.class);
	}

	public SimpleConverter() {
		super();
	}

	public SimpleConverter(Class<?>... types) {
		super(types);
	}

	@Override
	protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
		return super.isSupported(c, optionalExtraInfo);
	}

	@Override
	public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
		LOGGER.warn("Decode isn't implemented for {} to {} class...", fromDBObject, targetClass);
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (null == value) return null;

		final Class<? extends Object> clazz = value.getClass();
		LOGGER.debug("Encode {} (class={})", value, clazz);

		// Primitives classes, enums, Strings ...
		if (clazz.isPrimitive() || primitives.contains(clazz) || clazz.isEnum() || value instanceof String) {
			return value;
		}

		// Complexes cases
		EntityMetaData metaData = getMetaData(clazz);
		DBObject dbObj = null;

		if (value instanceof Collection) {
			// Collection (list and set)
			dbObj = encodeCollection(clazz, (Collection<Object>) value, optionalExtraInfo);

		} else {
			// Complex type
			dbObj = encodeComplexeType(clazz, value, metaData);
		}

		return dbObj;
	}

	/**
	 * Find meta data on this entity class.
	 *
	 * @param clazz
	 * @return Never null, but only collectionName may be filled.
	 */
	public EntityMetaData getMetaData(Class<? extends Object> clazz) {
		final String key = clazz.getName();

		if (!metaDatas.containsKey(key)) {
			EntityMetaData meta = new EntityMetaData(clazz);
			meta.setCollectionName(EntityUtils.getCollectionName(clazz));

			metaDatas.put(key, meta);
		}

		return metaDatas.get(key);
	}

	/**
	 * Encode object to {@link DBObject}. Complete metaData if it isn't initialized.
	 *
	 * @param clazz
	 * @param value
	 * @param metaData
	 * @return
	 */
	private DBObject encodeComplexeType(final Class<? extends Object> clazz, Object value, EntityMetaData metaData) {
		DBObject dbObj = new BasicDBObject();

		for (Method m : clazz.getMethods()) {
			if (m.getName().startsWith("get") && !"getClass".equals(m.getName())) {
				final String fieldName = StringUtils.uncapitalize(m.getName().substring(3));

				if (!metaData.isInitialised()) {
					final Field field = getField(clazz, fieldName);
					if (null != field) {
						if (null != field.getAnnotation(Id.class)) {
							metaData.setIdFieldName(fieldName);
						}
					}
				}

				try {
					final Object encodedValue = encode(m.invoke(value));
					if (null != encodedValue) {
						dbObj.put(getPropertyName(metaData, fieldName), encodedValue);
					}

				} catch (MappingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.info("No readable property {} : {}", fieldName, e.getMessage());
				}
			}

		}

		metaData.setInitialised(true);

		return dbObj;
	}

	private String getPropertyName(EntityMetaData metaData, String fieldName) {
		return metaData.getPropertyNames().containsKey(fieldName) ? metaData.getPropertyNames().get(fieldName) : fieldName;
	}

	/**
	 * Looking for field from its name on class and super class.
	 *
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	private Field getField(Class<? extends Object> clazz, String fieldName) {
		Class<?> c = clazz;

		Field f = null;

		do {
			try {
				f = c.getField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
			}

		} while (null == f && (c = c.getSuperclass()) != null);

		return f;
	}

	private DBObject encodeCollection(Class<? extends Object> clazz, Collection<Object> value, MappedField optionalExtraInfo) {
		if (value.isEmpty()) return null;

		BasicDBList db = new BasicDBList();

		for (Object item : value) {
			if (null != item) db.add(encode(item));
		}

		return db;
	}

}
