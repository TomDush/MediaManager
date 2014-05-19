package fr.dush.mediamanager.engine;

import javax.inject.Inject;

import org.junit.runner.RunWith;

import com.mongodb.DB;
import com.mongodb.DBCollection;

@RunWith(MongoDBJunitClassRunner.class)
public abstract class MongoJunitTest extends SpringJUnitTest {

    @Inject
    private DB db;

    protected DB getDb() {
        return db;
    }

    protected DBCollection getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }
}
