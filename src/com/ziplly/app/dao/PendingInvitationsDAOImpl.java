package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.ziplly.app.model.PendingInvitations;
import com.ziplly.app.model.PendingInvitationsDTO;

public class PendingInvitationsDAOImpl implements PendingInvitationsDAO {

	@Override
	public void save(PendingInvitations pi) {
		Preconditions.checkArgument(pi != null);
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			em.getTransaction().begin();
			em.persist(pi);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public List<PendingInvitationsDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery("from PendingInvitation");
			@SuppressWarnings("unchecked")
			List<PendingInvitations> result = query.getResultList();
			return EntityUtil.clonePendingInvidationList(result);
		} finally {
			em.close();
		}
	}
}
