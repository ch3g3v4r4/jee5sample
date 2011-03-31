package org.freejava.podcaster.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.freejava.podcaster.dao.GenericDao;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class GenericDaoImpl<T, ID extends Serializable> implements
        GenericDao<T, ID> {

    private Class<ID> primaryClass;
    private Class<T> persistentClass;
    private PrimaryIndex<ID, T> primaryIndex;

    @SuppressWarnings("unchecked")
    public GenericDaoImpl(EntityStore store) throws Exception {
        Type[] typeArgs = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        this.persistentClass = (Class<T>) typeArgs[0];
        this.primaryClass = (Class<ID>) typeArgs[1];
        this.primaryIndex = store
                .getPrimaryIndex(primaryClass, persistentClass);
    }

    public void add(T entity) throws Exception {
        primaryIndex.put(entity);
    }

    public void addAll(List<T> entities) throws Exception {
        for (T entity : entities) {
            add(entity);
        }
    }

    public T findById(ID id) throws Exception {
        return primaryIndex.get(id);
    }

    public List<T> findAll() throws Exception {

        List<T> result = new ArrayList<T>();

        EntityCursor<T> items = primaryIndex.entities();
        try {
            for (T item : items) {
                result.add(item);
            }
        } finally {
            items.close();
        }

        return result;
    }

    public void remove(ID id) throws Exception {
        primaryIndex.delete(id);
    }

    public void removeAll() throws Exception {
        List<ID> ids = new ArrayList<ID>();
        EntityCursor<ID> keyCursor = primaryIndex.keys();
        try {
            for (ID id : keyCursor) {
                ids.add(id);
            }
        } finally {
            keyCursor.close();
        }
        for (ID id : ids) {
            remove(id);
        }
    }
}
