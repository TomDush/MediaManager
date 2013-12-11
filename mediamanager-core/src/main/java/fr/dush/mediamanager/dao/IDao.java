package fr.dush.mediamanager.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Basic interface for DAO. Methods are implemented in {@link fr.dush.mediamanager.dao.mongodb.AbstractDAO}
 *
 * @author Thomas Duchatelle
 * @see fr.dush.mediamanager.dao.mongodb.AbstractDAO
 *
 */
public interface IDao<T, K> {

	/**
	 * Find entity by internal ID
	 *
	 *
     *
     * @param id
     * @return
	 */
	public T findById(K id);

	public void save(T dto);

	/**
	 * Find all persisted entities.
	 *
	 * @return
	 */
	public List<T> findAll();

	public long count();

	public void delete(K key);

}