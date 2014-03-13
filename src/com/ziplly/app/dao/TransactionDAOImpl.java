package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;

public class TransactionDAOImpl implements TransactionDAO {

	@Override
	public TransactionDTO save(Transaction txn) throws DuplicateException {
		if (txn == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();

			Query query =
			    em
			        .createQuery("from Transaction where seller.accountId = :accountId and status = :status");
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
		} finally {
			em.close();
		}
	}

	@Override
	public Transaction findById(Long transactionId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			return em.find(Transaction.class, transactionId);
		} finally {
			em.close();
		}
	}

	@Override
	public Transaction findByAccountId(Long accountId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createNamedQuery("findTransactionByAccount");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TransactionStatus.CANCELLED);
			return (Transaction) query.getSingleResult();
		} finally {
			em.close();
		}
	}

}
