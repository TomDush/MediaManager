package fr.dush.mediamanager.engine.mongodb;

import static com.google.common.collect.Lists.*;
import static org.fest.assertions.api.Assertions.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBObject;

import fr.dush.mediamanager.engine.MongoJunitTest;

public class MongoDBDatasetManagerTest extends MongoJunitTest {

	private static final String COLLECTION_TEST = "ConfigTest";

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBDatasetManagerTest.class);

	@Inject
	private MongoDBDatasetManager manager;

	@Inject
	private DB db;

	@Test
	public void testLoading() throws Exception {
		// Clear
		manager.clearCollection(COLLECTION_TEST);
		assertThat(db.getCollection(COLLECTION_TEST).find().toArray()).isEmpty();

		// Load
		manager.loadCollection(COLLECTION_TEST, newArrayList("configuration/config-test.json"));

		final List<DBObject> objs = db.getCollection(COLLECTION_TEST).find().toArray();
		LOGGER.debug("Loaded : {}", objs);
		assertThat(objs).hasSize(2);
	}

}
