package fr.dush.mediamanager.dao.mongodb.converters;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.code.morphia.converters.SimpleValueConverter;
import com.google.code.morphia.converters.TypeConverter;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * Simple converter using reflection...
 *
 * @author Thomas Duchatelle
 *
 */
public class PathConverter extends TypeConverter implements SimpleValueConverter {

	public PathConverter() {
		super(Path.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
		return Paths.get(fromDBObject.toString());
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		return value.toString();
	}

	@Override
	protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
		return super.isSupported(c, optionalExtraInfo);
	}

}
