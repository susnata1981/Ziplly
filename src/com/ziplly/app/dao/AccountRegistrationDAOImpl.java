package com.ziplly.app.dao;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountRegistration;
import com.ziplly.app.model.AccountRegistration.AccountRegistrationStatus;
import com.ziplly.app.model.AccountStatus;

public class AccountRegistrationDAOImpl extends BaseDAO implements AccountRegistrationDAO {
	private Logger logger = Logger.getLogger(AccountRegistrationDAOImpl.class.getCanonicalName());


	@Inject
	AccountRegistrationDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(AccountRegistration ar) {
		if (ar == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(ar);
		em.getTransaction().commit();
	}

	@Override
	public AccountRegistration findById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}

		Query query = getEntityManager().createQuery("from AccountRegistration where id = :id");
		query.setParameter("id", id);
		AccountRegistration result = (AccountRegistration) query.getSingleResult();
		return result;
	}

	@Override
	public AccountRegistration findByEmail(String email) {
		if (email == null) {
			throw new IllegalArgumentException();
		}

		Query query =
		    getEntityManager()
		        .createQuery(
		            "from AccountRegistration where email = :email and status = :status order by timeCreated desc");
		query.setParameter("email", email);
		query.setParameter("status", AccountRegistrationStatus.UNUSED.name());
		// @SuppressWarnings("unchecked")
		// List<AccountRegistration> result = query.getResultList();
		//
		// if (result.size() > 0) {
		// return result.get(0);
		// }
		// return null;
		AccountRegistration ar = (AccountRegistration) query.getSingleResult();
		return ar;
	}

	@Deprecated
	@Override
	public AccountRegistration findByEmailAndCode(String email, long code) {
		if (email == null) {
			throw new IllegalArgumentException();
		}

		// EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query =
		    getEntityManager().createQuery(
		        "from AccountRegistration where email = :email and status = :status and code = :code");
		query.setParameter("email", email);
		query.setParameter("code", code);
		query.setParameter("status", AccountRegistrationStatus.ACTIVE);
		AccountRegistration result = (AccountRegistration) query.getSingleResult();
		return result;
	}

	@Override
	public void update(AccountRegistration ar) {
		if (ar == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(ar);
		em.getTransaction().commit();
	}

	@Override
	public AccountRegistration findByEmailAndCode(String email, String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findAndVerifyAccount(Long id, String code) throws NotFoundException,
	    DuplicateException {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query query =
			    em
			        .createQuery("from AccountRegistration where accountId = :id and code = :code")
			        .setParameter("id", id)
			        .setParameter("code", code);

			AccountRegistration ar = (AccountRegistration) query.getSingleResult();

			if (ar.getStatus() == AccountRegistrationStatus.USED) {
				throw new DuplicateException();
			}

			ar.setStatus(AccountRegistrationStatus.USED);
			em.merge(ar);

			query =
			    em
			        .createQuery("from Account where account_id = :accountId")
			        .setParameter("accountId", id);
			Account account = (Account) query.getSingleResult();
			account.setStatus(AccountStatus.ACTIVE);
			account.setTimeUpdated(new Date());
			em.merge(account);

			em.getTransaction().commit();
		} catch (NoResultException e) {
			logger.warning(String.format(
			    "Couldn't find AccountRegistration with id %d, code %s",
			    id,
			    code));
			throw new NotFoundException();
		}
	}
}
