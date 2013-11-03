package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;

public class AccountDAOImpl implements AccountDAO {
	private Logger logger = Logger.getLogger(AccountDAOImpl.class
			.getCanonicalName());

	@Inject
	public AccountDAOImpl() {
		// this.em = EntityManagerService.getInstance().getEntityManager();
		// this.em = em;
	}

	@Override
	public AccountDTO findByEmail(String email) throws NotFoundException {
		if (email == null) {
			logger.log(Level.ERROR, "Invalid arguements to findByEmail");
			throw new IllegalArgumentException(
					"Invalid arguement to findByEmail");
		}
		System.out.println("Looking for account with email:" + email);
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmail");
		query.setParameter("email", email);
		Account account = null;
		AccountDTO resp = null;
		try {
			account = (Account) query.getSingleResult();
		} catch (NoResultException ex) {
			System.out.println("Didn't find account");
			throw new NotFoundException();
		} finally {
			if (account == null) {
				throw new NotFoundException();
			}
			resp = EntityUtil.convert(account);
			em.close();
		}
		System.out.println("Found account with email:" + email);
		return resp;
	}

	@Override
	public AccountDTO findByEmailAndPassword(String email, String password)
			throws NotFoundException {
		if (email == null || password == null) {
			logger.log(Level.ERROR,
					"Invalid arguements to findByEmailAndPassword");
			throw new IllegalArgumentException(
					"Invalid arguements to findByEmailAndPassword");
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmailAndPassword");
		query.setParameter("email", email);
		query.setParameter("password", password);
		AccountDTO account = null;
		try {
			Account acct = (Account) query.getSingleResult();
			account = EntityUtil.convert(acct);
		} catch (NoResultException ex) {
			throw new NotFoundException();
		} finally {
			em.close();
		}

		return account;
	}

	@Override
	public AccountDTO findById(Long accountId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findAccountById");
		query.setParameter("accountId", accountId);
		Account account = (Account) query.getSingleResult();
		return EntityUtil.convert(account);
	}

	@Override
	public AccountDTO save(Account account) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.persist(account);
		em.getTransaction().commit();
		em.close();
		return EntityUtil.convert(account);
	}

	@Override
	public AccountDTO update(Account account) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		Account acct = null;
		AccountDTO result = null;
		try {
			acct = em.merge(account);
			result = EntityUtil.convert(acct);
		} catch (NoResultException ex) {
			throw new IllegalArgumentException();
		} finally {
			em.getTransaction().commit();
			em.close();
		}
		return result;
	}

	@Override
	public List<Account> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findAllAccounts");
		return query.getResultList();
	}

	private Interest getInterest(Activity activity) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em
				.createQuery("from Interest where activity = :activity");
		query.setParameter("activity", activity);

		return (Interest) query.getSingleResult();
	}

	@Override
	public List<PersonalAccountDTO> findByZip(int zip) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createQuery("from PersonalAccount where zip = :zip");
		query.setParameter("zip", zip);

		@SuppressWarnings("unchecked")
		List<PersonalAccount> accounts = (List<PersonalAccount>)query.getResultList();
		List<PersonalAccountDTO> response = Lists.newArrayList();
		for(PersonalAccount pa : accounts) {
			response.add(EntityUtil.clone(pa));
		}
		return response;
	}
}
