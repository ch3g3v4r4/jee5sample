package com.fcg.style3.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.fcg.style3.dao.GenericDAO;

public abstract class GenericDAOImpl<T, ID extends Serializable> extends HibernateTemplate implements
        GenericDAO<T, ID>  {

    private Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }
//
//    public T loadById(ID id) {
//        return sessionFactory.find(persistentClass, id);
//    }
//
    public void persist1(T entity) {
        Session session = getSession();

        System.out.println("TRAN:" + SessionFactoryUtils.isSessionTransactional(session, getSessionFactory()));

        super.persist(entity);
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
