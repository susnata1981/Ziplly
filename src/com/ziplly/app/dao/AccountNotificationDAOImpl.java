package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.ReadStatus;

public class AccountNotificationDAOImpl extends BaseDAO implements AccountNotificationDAO {

	@Inject
	public AccountNotificationDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(an);
		em.getTransaction().commit();
	}

	@Override
	public List<AccountNotificationDTO> findAccountNotificationByAccountId(Long recipientId) {
		Preconditions.checkArgument(
		    recipientId != null,
		    "Invalid argument to findAccountNotificationByAccountId");

		Query query =
		    getEntityManager()
		        .createQuery(
		            "from AccountNotification where recipient.accountId = :recipientId and readStatus = :readStatus");
		query.setParameter("recipientId", recipientId);
		query.setParameter("readStatus", ReadStatus.UNREAD.name());

		@SuppressWarnings("unchecked")
		List<AccountNotification> anList = query.getResultList();
		List<AccountNotificationDTO> result = EntityUtil.cloneAccountNotificationList(anList);
		return result;
	}

	@Override
	public void update(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(an);
		em.getTransaction().commit();
	}

	@Override
	public AccountNotification findAccountNotificationByConversationId(Long conversationId) {
		Preconditions.checkArgument(
		    conversationId != null,
		    "Invalid argument to findAccountNotificationByAccountId");
		Query query =
		    getEntityManager().createQuery(
		        "from AccountNotification where conversation.id = :conversationId");
		query.setParameter("conversationId", conversationId);
		try {
			AccountNotification result = (AccountNotification) query.getSingleResult();
			return result;
		} catch (NoResultException nre) {
			return null;
		}
	}
}
