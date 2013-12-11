package fr.dush.mediamanager.dao.mongodb;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import fr.dush.mediamanager.dao.IDao;
import lombok.AccessLevel;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * Provide basic CRUD operation on database.
 *
 * @param <T>
 * @param <K>
 */
public abstract class AbstractDAO<T, K> implements IDao<T, K> {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private Jongo jongo;

    private final Class<T> clazz;

    @Getter(AccessLevel.PROTECTED)
    private MongoCollection collection;

    protected AbstractDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @PostConstruct
    public void initCollection() {
        collection = jongo.getCollection(getCollectionName());
    }

    /** Get collection name of this class */
    protected String getCollectionName() {
        return EntityUtils.getCollectionName(clazz);
    }

    @Override
    public T findById(K id) {
        if (id instanceof ObjectId) {
            return collection.findOne((ObjectId) id).as(clazz);
        } else {
            return collection.findOne("{_id : #}", id).as(clazz);
        }
    }

    @Override
    public void save(T dto) {
        collection.save(dto);
    }

    @Override
    public List<T> findAll() {
        return newArrayList(collection.find().as(clazz));
    }

    @Override
    public long count() {
        return collection.count();
    }

    @Override
    public void delete(K key) {
        if(key instanceof ObjectId) {
            collection.remove((ObjectId) key);
        } else {
            collection.remove("{ _id : # }", key);
        }
    }

    /**
     * Format string array to JSON list.
     */
    protected static String asJsonList(String... args) {
        return "[ '" + Joiner.on("', '").skipNulls().join(args) + "' ]";
    }

    protected static String asJsonList(Iterable<String> args) {
        return "[ '" + Joiner.on("', '").skipNulls().join(args) + "' ]";
    }

    /**
     * Format object array to JSON list.
     */
    protected static <T> String asJsonList(T[] args, Function<T, String> function) {
        return "[ " + Joiner.on(", ").skipNulls().join(transform(Arrays.asList(args), function)) + " ]";
    }
}