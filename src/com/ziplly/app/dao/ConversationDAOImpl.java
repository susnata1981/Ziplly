package com.ziplly.app.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.Message;

public class ConversationDAOImpl implements ConversationDAO {

	@Override
	public List<ConversationDTO> getConversationForAccount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findConversationByAccountId");
		query.setParameter("receiverAccountId", accountId);
		query.setParameter("senderAccountId", accountId);
		
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
		return result;
	}

	@Override
	public void save(Conversation conversation) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.merge(conversation);
		em.getTransaction().commit();
		em.close();
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
	}

	@Override
	public Long getUnreadConversationForAccount(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findUnreadConversationByAccountId");
		query.setParameter("receiverAccountId", accountId);
		query.setParameter("status", ConversationStatus.UNREAD);
		return (Long)query.getSingleResult();
	}
}
