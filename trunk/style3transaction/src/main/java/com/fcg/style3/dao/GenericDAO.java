package com.fcg.style3.dao;

import java.io.Serializable;

public interface GenericDAO<T, ID extends Serializable> {
//
//    T loadById(ID id);
//
    void persist1(T entity);
//
//    void update(T entity);
//
//    void delete(T entity);
//
//    List<T> loadAll();

}
