package fr.dush.mediamanager.engine;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(MongoDBJunitClassRunner.class)
public abstract class MongoJunitTest extends SpringJUnitTest {

    @Inject
    protected DB db;

    protected DBCollection getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }
}
