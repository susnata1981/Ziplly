package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.server.model.jpa.Conversation;
import com.ziplly.app.server.model.jpa.Message;

public class ConversationDAOImpl extends BaseDAO implements ConversationDAO {

	@Inject
	public ConversationDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public List<ConversationDTO> getConversationForAccount(Long accountId,
	    ConversationType type,
	    int start,
	    int pageSize) {

		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		Query query = null;

		switch (type) {
			case SENT:
				query =
				    em
				        .createQuery("from Conversation where sender.accountId = :senderAccountId order by timeCreated desc");
				query.setParameter("senderAccountId", accountId);
				break;
			case RECEIVED:
				query =
				    em
				        .createQuery("from Conversation where receiver.accountId = :receiverAccountId order by timeCreated desc");
				query.setParameter("receiverAccountId", accountId);
				break;
			case ALL:
			default:
				query =
				    em
				        .createQuery("from Conversation where receiver.accountId = :receiverAccountId or sender.accountId = :senderAccountId order by timeCreated desc");
				query.setParameter("receiverAccountId", accountId).setParameter(
				    "senderAccountId",
				    accountId);
		}

		query.setFirstResult(start).setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Conversation> conversations = (List<Conversation>) query.getResultList();

		List<ConversationDTO> result = Lists.newArrayList();
		for (Conversation c : conversations) {
			Collections.sort(c.getMessages(), new Comparator<Message>() {
				@Override
				public int compare(Message m1, Message m2) {
					if (m1.getTimeCreated().before(m2.getTimeCreated())) {
						return -1;
					}
					return 1;
				}
			});
			ConversationDTO clone = EntityUtil.clone(c);
			clone.setIsSender(false);
			if (clone.getSender().getAccountId() == accountId) {
				clone.setIsSender(true);
			}
			result.add(clone);
		}
		return result;
	}

	@Override
	public Long getTotalConversationCount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		Query query =
		    getEntityManager().createNativeQuery(
		        "select count(*) from conversation where receiver_account_id = :receiverAccountId "
		            + "or sender_account_id = :senderAccountId");
		query.setParameter("receiverAccountId", accountId);
		query.setParameter("senderAccountId", accountId);
		BigInteger result = (BigInteger) query.getSingleResult();
		return result.longValue();
	}

	@Override
	public Long getTotalConversationCountOfType(ConversationType type, Long accountId) {
		EntityManager em = getEntityManager();
		Query query;
		switch (type) {
			case SENT:
				query =
				    em
				        .createNativeQuery("select count(*) from conversation where sender_account_id = :senderAccountId");
				query.setParameter("senderAccountId", accountId);
				break;
			case RECEIVED:
			default:
				query =
				    em
				        .createNativeQuery("select count(*) from conversation where receiver_account_id = :receiverAccountId");
				query.setParameter("receiverAccountId", accountId);
		}
		BigInteger result = (BigInteger) query.getSingleResult();
		return result.longValue();
	}

	@Transactional
	@Override
	public Conversation save(Conversation conversation) {
		EntityManager em = getEntityManager();
		Conversation result = em.merge(conversation);
		em.flush();
		return result;
	}

	@Transactional
	@Override
	public void markConversationAsRead(Long conversationId) {
		if (conversationId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("findConversationById");
		query.setParameter("id", conversationId);
		Conversation c = (Conversation) query.getSingleResult();
		c.setStatus(ConversationStatus.READ);
		em.merge(c);
	}

	@Override
	public Long getUnreadConversationCountForAccount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("findConversationCountByAccountIdAndStatus");
		query.setParameter("receiverAccountId", accountId).setParameter(
		    "status",
		    ConversationStatus.UNREAD.name());
		Long result = (Long) query.getSingleResult();
		return result;
	}

	@Override
	public ConversationDTO findConversationById(Long conversationId) {
		if (conversationId == null) {
			throw new IllegalArgumentException();
		}

		Query query = getEntityManager().createQuery("from Conversation where id = :conversationId");
		query.setParameter("conversationId", conversationId);
		Conversation c = (Conversation) query.getSingleResult();
		ConversationDTO result = EntityUtil.clone(c);
		return result;
	}
}
