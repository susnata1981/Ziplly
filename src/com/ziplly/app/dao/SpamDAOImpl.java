package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.Spam;
import com.ziplly.app.model.SpamDTO;

public class SpamDAOImpl extends BaseDAO implements SpamDAO {

	@Inject
	public SpamDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(Spam spam) {
		if (spam == null) {
			throw new IllegalArgumentException("Invalid argument to save(...)");
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(spam);
		em.getTransaction().commit();
	}

	@Override
	public List<SpamDTO> getAll() {
		Query query = getEntityManager().createQuery("from Spam order by timeCreated desc");
		@SuppressWarnings("unchecked")
		List<Spam> result = query.getResultList();
		List<SpamDTO> response = Lists.newArrayList();
		for (Spam s : result) {
			response.add(EntityUtil.clone(s));
		}
		return response;
	}
}
