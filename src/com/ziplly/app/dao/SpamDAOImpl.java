package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.ziplly.app.model.Spam;
import com.ziplly.app.model.SpamDTO;

public class SpamDAOImpl implements SpamDAO {

	@Override
	public void save(Spam spam) {
		if (spam == null) {
			throw new IllegalArgumentException("Invalid argument to save(...)");
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(spam);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public List<SpamDTO> getAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery("from Spam order by timeCreated desc");
			@SuppressWarnings("unchecked")
			List<Spam> result = query.getResultList();
			List<SpamDTO> response = Lists.newArrayList();
			for (Spam s : result) {
				response.add(EntityUtil.clone(s));
			}
			return response;
		} finally {
			em.close();
		}
	}
}
