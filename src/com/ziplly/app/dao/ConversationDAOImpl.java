package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.model.Message;

public class ConversationDAOImpl implements ConversationDAO {

	@Override
	public List<ConversationDTO> getConversationForAccount(
			Long accountId,
			ConversationType type,
			int start, 
			int pageSize) {
		
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = null;
		
		switch(type) {
		case SENT:
			query = em.createQuery("from Conversation where sender.accountId = :senderAccountId order by timeCreated desc");
			query.setParameter("senderAccountId", accountId);
			break;
		case RECEIVED:
			query = em.createQuery("from Conversation where receiver.accountId = :receiverAccountId order by timeCreated desc");
			query.setParameter("receiverAccountId", accountId);
			break;
		case ALL:
		default:
			query = em.createQuery("from Conversation where receiver.accountId = :receiverAccountId or sender.accountId = :senderAccountId order by timeCreated desc");
			query.setParameter("receiverAccountId", accountId)
			.setParameter("senderAccountId", accountId);
		}
	
		query.setFirstResult(start).setMaxResults(pageSize);
	
		@SuppressWarnings("unchecked")
		List<Conversation> conversations = (List<Conversation>) query
				.getResultList();
		
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
		em.close();
		return result;
	}

	@Override
	public Long getTotalConversationCount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNativeQuery("select count(*) from Conversation where receiver_account_id = :receiverAccountId "
				+ "or sender_account_id = :senderAccountId");
		query.setParameter("receiverAccountId", accountId);
		query.setParameter("senderAccountId", accountId);
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.longValue();
	}

	@Override
	public Long getTotalConversationCountOfType(ConversationType type, Long accountId) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query;
		switch(type) {
		case SENT:
			query = em.createNativeQuery("select count(*) from Conversation where sender_account_id = :senderAccountId");
			query.setParameter("senderAccountId", accountId);
			break;
		case RECEIVED:
		default:
			query = em.createNativeQuery("select count(*) from Conversation where receiver_account_id = :receiverAccountId");
			query.setParameter("receiverAccountId", accountId);
		}
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.longValue();
	}
	
	@Override
	public Conversation save(Conversation conversation) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		Conversation result = em.merge(conversation);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return result;
	}

	@Override
	public void markConversationAsRead(Long conversationId) {
		if (conversationId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findConversationById");
		query.setParameter("id", conversationId);
		
		Conversation c = (Conversation) query.getSingleResult();
		em.getTransaction().begin();
		c.setStatus(ConversationStatus.READ);
		em.merge(c);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public Long getUnreadConversationCountForAccount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findConversationCountByAccountIdAndStatus");
		query.setParameter("receiverAccountId", accountId)
		    .setParameter("status", ConversationStatus.UNREAD.name());
		
		Long result = (Long)query.getSingleResult();
		em.close();
		return result;
	}

	@Override
	public ConversationDTO findConversationById(Long conversationId) {
		if (conversationId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createQuery("from Conversation where id = :conversationId");
		query.setParameter("conversationId", conversationId);
		Conversation c = (Conversation) query.getSingleResult();
		ConversationDTO result = EntityUtil.clone(c);
		em.close();
		return result;
	}
}
