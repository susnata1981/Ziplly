package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ziplly.app.model.AccountRegistration;
import com.ziplly.app.model.AccountRegistration.AccountRegistrationStatus;

public class AccountRegistrationDAOImpl implements AccountRegistrationDAO {

	@Override
	public void create(AccountRegistration ar) {
		if (ar == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(ar);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public AccountRegistration findById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from AccountRegistration where id = :id");
		query.setParameter("id", id);
		AccountRegistration result = (AccountRegistration) query.getSingleResult();
		em.close();
		return result;
	}

	@Override
	public AccountRegistration findByEmail(String email) {
		if (email == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createQuery("from AccountRegistration where email = :email and status = :status order by timeCreated desc");
		query.setParameter("email", email);
		query.setParameter("status", AccountRegistrationStatus.ACTIVE);
		@SuppressWarnings("unchecked")
		List<AccountRegistration> result = query.getResultList();
		em.close();
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public AccountRegistration findByEmailAndCode(String email, long code) {
		if (email == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createQuery("from AccountRegistration where email = :email and status = :status and code = :code");
		query.setParameter("email", email);
		query.setParameter("code", code);
		query.setParameter("status", AccountRegistrationStatus.ACTIVE);
		AccountRegistration result = (AccountRegistration) query.getSingleResult();
		em.close();
		return result;
	}

	@Override
	public void update(AccountRegistration ar) {
		if (ar == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.merge(ar);
		em.getTransaction().commit();
		em.close();
	}
}
