package com.example.sample.model.business;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
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
        return "How do you do: " + name + "!";
    }

	public User getUser(Long userId) {
		User user = em.find(User.class, Long.valueOf(userId));
		user.setEmail("set@bycode2");
		return user;
	}

	public Collection<User> getUsers() {
		Collection<User> users = (Collection<User>) em.createQuery("SELECT u FROM User u").getResultList();

		return users;
	}

	@AroundInvoke
	public Object profile(InvocationContext inv) throws Exception {
		long time = System.currentTimeMillis();
		try {
			return inv.proceed();
		} finally {
			long totalTime = System.currentTimeMillis() - time;
			System.out.println(inv.getMethod() + " took " + totalTime + " ms.");
		}
	}
}
