package fr.dush.mediamanager.dao.mongodb.converters;

import static com.google.common.collect.Maps.*;

import java.util.Map;

import lombok.Data;

import com.google.code.morphia.annotations.Id;

/**
 * Meta data on <i>Morphia</i> entities.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
public class EntityMetaData {

	/** Target class of this informations */
	private Class<?> clazz;

	/** Collection's name on MongoDB */
	private String collectionName;

	/** Id field name if defined with {@link Id} annotation. */
	private String idFieldName;

	/** Relation between field name and property name */
	private Map<String, String> propertyNames = newHashMap();

	/** If this object is fully initialized */
	private boolean initialised = false;

	public EntityMetaData(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
		propertyNames.put(idFieldName, "_id");
	}

}
