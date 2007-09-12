package com.fcg.trilogy.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * Manager class to manage RFP objects in database.
 *
 */
public class RFPManager {

	private EntityManagerFactory emf;

	public RFPManager() {
		emf = Persistence.createEntityManagerFactory("manager1");
	}

	@Override
	protected void finalize() throws Throwable {
		if (emf != null) {
			emf.close();
			emf = null;
		}
		super.finalize();
	}

	protected EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public RFP persist(RFP rfp) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(rfp);

		tx.commit();
		em.close();
		return rfp;
	}

	public RFP find(Long id) {
		EntityManager em = getEntityManager();

		RFP rfp = em.find(RFP.class, id);

		em.close();
		return rfp;
	}

	public Collection<RFP> findAll() {
		EntityManager em = getEntityManager();
		Query q = em.createQuery("SELECT r FROM RFP r");
		Collection<RFP> result = q.getResultList();
		em.close();
		return result;
	}

	public Collection<RFP> findWithDate(Date date) {
		EntityManager em = getEntityManager();
		Query q = em.createQuery("SELECT r FROM RFP r WHERE r.date = ?1");
		q.setParameter(1, date, TemporalType.DATE);
		Collection<RFP> result = q.getResultList();
		em.close();
		return result;
	}

	public Collection<RFP> search(Map<String, String> criteria) {

		EntityManager em = getEntityManager();

		Collection<RFP> result;
		Query q;
		if (criteria.isEmpty()) {
			q = em.createQuery("SELECT r FROM RFP r");
		} else {
			String query = "SELECT r FROM RFP r WHERE ";
			boolean firstCondition = true;
			int index = 1;
			List<String> conditionValues = new ArrayList<String>();
			for (String key : criteria.keySet()) {
				if (!firstCondition) {
					query += " AND ";
				} else {
					firstCondition = false;
				}
				query += " UPPER(r." + key + ") LIKE UPPER(?" + index + ") ";
				index++;
				conditionValues.add(criteria.get(key));
			}
			index = 1;
			q = em.createQuery(query);
			for (String value : conditionValues) {
				q.setParameter(index, "%" + value + "%");
				index++;
			}
		}
		result = q.getResultList();
		em.close();
		return result;
	}
}
