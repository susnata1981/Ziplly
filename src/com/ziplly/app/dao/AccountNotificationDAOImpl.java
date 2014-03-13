package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.ReadStatus;

public class AccountNotificationDAOImpl implements AccountNotificationDAO {

	@Override
	public void save(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(an);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public List<AccountNotificationDTO> findAccountNotificationByAccountId(Long recipientId) {
		Preconditions.checkArgument(
		    recipientId != null,
		    "Invalid argument to findAccountNotificationByAccountId");
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			Query query =
			    em
			        .createQuery("from AccountNotification where recipient.accountId = :recipientId and readStatus = :readStatus");
			query.setParameter("recipientId", recipientId);
			query.setParameter("readStatus", ReadStatus.UNREAD.name());

			@SuppressWarnings("unchecked")
			List<AccountNotification> anList = query.getResultList();
			List<AccountNotificationDTO> result = EntityUtil.cloneAccountNotificationList(anList);
			return result;
		} finally {
			em.close();
		}
	}

	@Override
	public void update(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(an);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public AccountNotification findAccountNotificationByConversationId(Long conversationId) {
		Preconditions.checkArgument(
		    conversationId != null,
		    "Invalid argument to findAccountNotificationByAccountId");
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query =
		    em.createQuery("from AccountNotification where conversation.id = :conversationId");
		query.setParameter("conversationId", conversationId);
		try {
			AccountNotification result = (AccountNotification) query.getSingleResult();
			return result;
		} catch (NoResultException nre) {
			return null;
		} finally {
			em.close();
		}
	}
}
