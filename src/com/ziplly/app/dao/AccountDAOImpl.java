package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;

public class AccountDAOImpl implements AccountDAO {
	private Logger logger = Logger.getLogger(AccountDAOImpl.class.getCanonicalName());
//	private EntityManager em;
	
	@Inject
	public AccountDAOImpl() {
//		this.em = EntityManagerService.getInstance().getEntityManager();
//		this.em = em;
	}
	
	@Override
	public Account findByEmail(String email) throws NotFoundException {
		if (email == null) {
			logger.log(Level.ERROR, "Invalid arguements to findByEmail");
			throw new IllegalArgumentException("Invalid arguement to findByEmail");
		}
		
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
	public Account findByEmailAndPassword(String email, String password) throws NotFoundException {
		if (email == null || password == null) {
			logger.log(Level.ERROR, "Invalid arguements to findByEmailAndPassword");
			throw new IllegalArgumentException("Invalid arguements to findByEmailAndPassword");
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmailAndPassword");
		query.setParameter("email", email);
		query.setParameter("password", password);
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
