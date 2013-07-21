package fr.dush.mediamanager.engine;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import fr.dush.mediamanager.engine.mongodb.EmbeddedMongoDbControler;

@RunWith(MongoDBJunitClassRunner.class)
public abstract class MongoJunitTest {

	@Inject
	private EmbeddedMongoDbControler embeddedMongoDbControler;

	@Before
	public void setUp() {
		// Start mongoDB
//		embeddedMongoDbControler.startEmbeddedMogonDB();
	}

	@After
	public void tearDown() {
		// Stop mongoDB...
//		embeddedMongoDbControler.stopEmbeddedMogonDB();
	}
}
