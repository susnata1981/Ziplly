package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.server.model.jpa.AccountNotification;

public class AccountNotificationDAOImpl extends BaseDAO implements AccountNotificationDAO {

	@Inject
	public AccountNotificationDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Transactional
	@Override
	public void save(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = getEntityManager();
		em.merge(an);
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

	@Transactional
	@Override
	public void update(AccountNotification an) {
		Preconditions.checkArgument(an != null, "Invalid argument to save");
		EntityManager em = getEntityManager();
		em.merge(an);
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
