package com.example.sample.model.business;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
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
		return user;
	}

	public Collection<User> getUsers() {
		Collection<User> users = (Collection<User>) em.createQuery("SELECT u FROM User u").getResultList();
		return users;
	}

	@Resource
	private SessionContext context;

	public void shopping() {
		Cart cardBean = (Cart) context.lookup("cart");
		cardBean.beginShopping("Thai Ha");
		cardBean.addItem("Hat");
		cardBean.addItem("Shoes");
		cardBean.finishShopping();

	}

//	@AroundInvoke
//	public Object profile(InvocationContext inv) throws Exception {
//		long time = System.currentTimeMillis();
//		try {
//			return inv.proceed();
//		} finally {
//			long totalTime = System.currentTimeMillis() - time;
//			System.out.println(inv.getMethod() + " took " + totalTime + " ms.");
//		}
//	}
}
