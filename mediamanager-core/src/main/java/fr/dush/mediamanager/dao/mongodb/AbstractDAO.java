package fr.dush.mediamanager.dao.mongodb;

import static com.google.common.collect.Lists.*;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.dush.mediamanager.dao.IDao;

public abstract class AbstractDAO<T, K> implements IDao<T, K> {

	@Inject
	protected Datastore ds;

	private final Class<T> clazz;

	public AbstractDAO(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T findById(K id) {
		return ds.get(clazz, id);
	}

	@Override
	public void save(T dto) {
		ds.save(dto);
	}

	@Override
	public List<T> findAll() {
		return ds.createQuery(clazz).asList();
	}

	@Override
	public long count() {
		return ds.getCount(clazz);
	}

	@Override
	public void delete(T dto) {
		ds.delete(dto);
	}

	/**
	 * Execute query written in native form : JSON.
	 *
	 * @param query JSON query
	 * @param args parameters to set into query (using String.format)
	 * @return Result list
	 */
	protected Query<T> createNativeQuery(String jsonQuery, Object... args) {
		QueryImpl<T> query = ((QueryImpl<T>) ds.createQuery(clazz));
		query.setQueryObject((DBObject) JSON.parse(String.format(jsonQuery, args)));

		return query;
	}

	/**
	 * Create basic Morphia query, for this class.
	 * @return
	 */
	protected Query<T> createQuery() {
		return ds.createQuery(clazz);
	}

	/**
	 * Format string array to JSON list.
	 * @param args
	 * @return
	 */
	protected static String asJsonList(String... args) {
		return "[ '" + Joiner.on("', '").skipNulls().join(args) + "' ]";
	}

	/**
	 * Format object array to JSON list.
	 * @param args
	 * @param function
	 * @return
	 */
	protected static <T> String asJsonList(T[] args, Function<T, String> function) {
		return "[ " + Joiner.on(", ").skipNulls().join(transform(Arrays.asList(args), function)) + " ]";
	}

}