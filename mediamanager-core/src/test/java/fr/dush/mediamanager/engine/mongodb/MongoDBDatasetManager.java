package fr.dush.mediamanager.engine.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fr.dush.mediamanager.dao.mongodb.EntityUtils;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Manage data sets in Mongo database.
 *
 * @author Thomas Duchatelle
 *
 */
@Component
public class MongoDBDatasetManager {

	@Inject
	private DB db;

	public void clearCollection(String collectionName) {
		db.getCollection(collectionName).drop();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadCollection(String collectionName, List<String> locations) throws IOException {
		for (String l : locations) {
			final String content = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(l).getFile()), "UTF8");
			final Object parsedFile = JSON.parse(content);

			if (parsedFile instanceof BasicDBList) {
				final Iterator it = ((BasicDBList) parsedFile).iterator();
				db.getCollection(collectionName).insert(newArrayList((Iterator<DBObject>) it));
			}
		}
	}

	public void initializeDataset(DatabaseScript script) {
		// Get collection name
		String collectionName = script.collectionName();
		if (isEmpty(collectionName) && null != script.clazz()) {
			collectionName = EntityUtils.getCollectionName(script.clazz());
		}

		if (isEmpty(collectionName)) throw new ConfigurationException("[TestCofiguration] No collection name specified for : " + script);

		// Clear if necessary
		if (script.clear()) {
			clearCollection(collectionName);
		}

		// Loading
		if (script.locations().length > 0) {
			try {
				loadCollection(collectionName, Arrays.asList(script.locations()));
			} catch (IOException e) {
				throw new ConfigurationException("[TestCofiguration] Invalid file names : " + script.locations(), e);
			}
		}

	}


}
