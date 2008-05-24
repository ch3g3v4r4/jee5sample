package com.fcg.style3.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fcg.style3.dao.GenericDAO;

@Transactional
public abstract class GenericDAOImpl<T, ID extends Serializable> implements
        GenericDAO<T, ID> {

    private Class<T> persistentClass;

    protected SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    @SuppressWarnings("unchecked")
    public T loadById(ID id) {
        return (T) sessionFactory.getCurrentSession().get(persistentClass, id);
    }

    public void persist(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(entity);
    }

    public void update(T entity) {
        sessionFactory.getCurrentSession().merge(entity);
    }

    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @SuppressWarnings("unchecked")
    public List<T> loadAll() {
        return sessionFactory.getCurrentSession().createQuery("select t from " + persistentClass.getSimpleName() + " t").list();
    }

}
