package com.fcg.style3.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fcg.style3.dao.GenericDAO;

@Transactional
public abstract class GenericDAOImpl<T, ID extends Serializable> implements
        GenericDAO<T, ID> {

    private Class<T> persistentClass;

    protected SessionFactory sessionFactory1;
    protected SessionFactory sessionFactory2;

    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setSessionFactory1(SessionFactory sessionFactory) {
        this.sessionFactory1 = sessionFactory;
    }
    public void setSessionFactory2(SessionFactory sessionFactory) {
        this.sessionFactory2 = sessionFactory;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }
//
//    public T loadById(ID id) {
//        return sessionFactory.find(persistentClass, id);
//    }
//
    public void persist(T entity) {
        Session session = sessionFactory1.getCurrentSession();
        session.merge(entity);
    }
//
//    public void update(T entity) {
//        entityManager.merge(entity);
//    }
//
//    public void delete(T entity) {
//        entityManager.remove(entity);
//    }
//
//    @SuppressWarnings("unchecked")
//    public List<T> loadAll() {
//        return entityManager.createQuery(
//                "select t from " + persistentClass.getSimpleName() + " t")
//                .getResultList();
//    }

}
