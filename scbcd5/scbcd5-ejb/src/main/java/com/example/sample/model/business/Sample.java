package com.example.sample.model.business;

import java.util.Collection;

import javax.ejb.Local;

import com.example.sample.model.vo.User;

@Local
public interface Sample {
    public String sayHello(String name);
    public User getUser(Long userId);
    public Collection<User> getUsers();
    public void shopping();
}
