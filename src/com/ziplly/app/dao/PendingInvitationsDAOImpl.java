package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.PendingInvitations;
import com.ziplly.app.model.PendingInvitationsDTO;

public class PendingInvitationsDAOImpl extends BaseDAO implements PendingInvitationsDAO {

	@Inject
	public PendingInvitationsDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(PendingInvitations pi) {
		Preconditions.checkArgument(pi != null);
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(pi);
		em.getTransaction().commit();
	}

	@Override
	public List<PendingInvitationsDTO> findAll() {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("from PendingInvitation");
		@SuppressWarnings("unchecked")
		List<PendingInvitations> result = query.getResultList();
		return EntityUtil.clonePendingInvidationList(result);
	}
}
