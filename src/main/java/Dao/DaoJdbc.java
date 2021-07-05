package Dao;

import java.util.List;
import java.util.Optional;

/**
 * interface for basic operation on database
 * @param <T>
 */
public interface DaoJdbc<T> {

    /**
     * Save object into database
     * <p>
     * @param t object to save
     * @return id of saved object
     */
    Optional<T> save(T t);

    /**
     * Delete object into database
     * <p>
     * @param id id of object to delete
     * @return id of deleted object
     */
    Optional<T> delete(long id);

    /**
     * Update object in database
     * <p>
     * @param t modified object from database
     * @return id of updated object
     */
    Optional<T> update(T t);

    /**
     * Return object from database
     * <p>
     * @param id id of wanted object
     * @return Optional<Object> of object with given id
     */
    Optional<T> get(long id);

    /**
     * Return all object from database
     * <p>
     * @return list of all object from database
     */
    List<T> getAll();
}
