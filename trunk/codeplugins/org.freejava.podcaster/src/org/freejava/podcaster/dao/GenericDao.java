package org.freejava.podcaster.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Generic DAO class for all specific DAO classes.
 * 
 * @param <T>
 *            entity type
 * @param <ID>
 *            primary key type
 */
public interface GenericDao<T, ID extends Serializable> {

    /**
     * Add an entity into persistence storage.
     * 
     * @param entity
     *            entity
     * @throws Exception
     *             if any error happen
     */
    void add(T entity) throws Exception;

    /**
     * Add provided entities to persistence storage.
     * 
     * @param entities
     *            entities
     * @throws Exception
     *             if any error happen
     */
    void addAll(List<T> entities) throws Exception;

    /**
     * Finds an entity from persistence storage.
     * 
     * @param id
     *            entity id
     * @return entity found entity or null
     * @throws Exception
     *             if any error happen
     */
    T findById(ID id) throws Exception;

    /**
     * Loads all entities from persistence storage.
     * 
     * @return a collection of entities
     * @throws Exception
     *             if any error happen
     */
    List<T> findAll() throws Exception;

    /**
     * Removes an entity from persistence storage.
     * 
     * @param id
     *            entity ID
     * @throws Exception
     *             if some error happens
     */
    void remove(ID id) throws Exception;

    /**
     * Removes all entities from persistence storage.
     * 
     * @throws Exception
     *             if some error happens
     */
    void removeAll() throws Exception;
}
