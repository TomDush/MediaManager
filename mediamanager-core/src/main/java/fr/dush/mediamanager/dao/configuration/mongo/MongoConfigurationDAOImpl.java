package fr.dush.mediamanager.dao.configuration.mongo;

import static com.google.common.collect.Lists.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.configuration.FieldSet;

@ApplicationScoped
public class MongoConfigurationDAOImpl implements IConfigurationDAO {

	private static final String COLLECTION_NAME = "Config";

	@Inject
	private DB db;

	@Override
	public List<Field> findByPackage(String packageName) {
		List<Field> fields = newArrayList();

		final DBCursor cursor = getCollection().find(new BasicDBObject("_id", packageName));
		if (cursor != null && cursor.hasNext()) {
			final DBObject values = (DBObject) cursor.next().get("values");

			for (String key : values.keySet()) {
				fields.add(new Field(key, (String) values.get(key)));
			}
		}

		return fields;
	}

	@Override
	public void save(FieldSet configuration) {
		// Mapping object
		DBObject obj = new BasicDBObject("_id", configuration.getPackageName());
		obj.put("name", configuration.getName());

		final BasicDBObject values = new BasicDBObject();
		obj.put("values", values);
		for (Field f : configuration.getFields().values()) {
			values.put(f.getKey(), f.getValue());
		}

		// Saving
		getCollection().save(obj);
	}

	private DBCollection getCollection() {
		return db.getCollection(COLLECTION_NAME);
	}

}
