package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;

public class TransactionDAOImpl extends BaseDAO implements TransactionDAO {

	@Inject
	public TransactionDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public TransactionDTO save(Transaction txn) throws DuplicateException {
		if (txn == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();

		Query query =
		    em.createQuery("from Transaction where seller.accountId = :accountId and status = :status");
		query.setParameter("accountId", txn.getSeller().getAccountId());
		query.setParameter("status", TransactionStatus.ACTIVE);

		boolean isAlreadyPaid = true;
		try {
			query.getSingleResult();
		} catch (NoResultException nre) {
			isAlreadyPaid = false;
		}

		if (isAlreadyPaid) {
			throw new DuplicateException();
		}

		em.persist(txn);
		em.getTransaction().commit();
		TransactionDTO result = EntityUtil.clone(txn);
		return result;
	}

	@Override
	public Transaction findById(Long transactionId) {
		return getEntityManager().find(Transaction.class, transactionId);
	}

	@Override
	public Transaction findByAccountId(Long accountId) {
		Query query = getEntityManager().createNamedQuery("findTransactionByAccount");
		query.setParameter("accountId", accountId);
		query.setParameter("status", TransactionStatus.CANCELLED);
		return (Transaction) query.getSingleResult();
	}
}
