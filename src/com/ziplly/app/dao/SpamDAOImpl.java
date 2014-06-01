package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.server.model.jpa.Spam;

public class SpamDAOImpl extends BaseDAO implements SpamDAO {

	@Inject
	public SpamDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Transactional
	@Override
	public void save(Spam spam) {
		if (spam == null) {
			throw new IllegalArgumentException("Invalid argument to save(...)");
		}

		EntityManager em = getEntityManager();
		em.persist(spam);
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
