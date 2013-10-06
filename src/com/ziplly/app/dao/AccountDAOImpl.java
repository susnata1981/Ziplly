package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;

public class AccountDAOImpl implements AccountDAO {

	@Override
	public Account findByEmail(String email) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmail");
		query.setParameter("email", email);
		Account account = null;
		try {
			account = (Account) query.getSingleResult();
		} catch(NoResultException ex) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
		
		return account;
	}

	@Override
	public Account findById(Long accountId)
			throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountById");
		query.setParameter("account_id", accountId);
		Account result = (Account) query.getSingleResult();
		return result;
	}

	@Override
	public void save(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(account);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void update(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.merge(account);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public List<Account> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAllAccounts");
		return query.getResultList();
	}
}
