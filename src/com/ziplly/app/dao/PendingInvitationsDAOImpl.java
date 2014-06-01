package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.PendingInvitationsDTO;
import com.ziplly.app.server.model.jpa.PendingInvitations;

public class PendingInvitationsDAOImpl extends BaseDAO implements PendingInvitationsDAO {

	@Inject
	public PendingInvitationsDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Transactional
	@Override
	public void save(PendingInvitations pi) {
		Preconditions.checkArgument(pi != null);
		EntityManager em = getEntityManager();
		em.persist(pi);
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
