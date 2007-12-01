package com.example.sample.model.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.sample.model.vo.User;

@Stateless
public class SampleBean implements Sample {
    @PersistenceContext
    private EntityManager em;

    public SampleBean() {
        // empty
    }

    public String sayHello(String name) {
        return "How do you do! " + name + "!";
    }

	public User getUser(Long userId) {
		return em.find(User.class, Long.valueOf(userId));
	}
}
