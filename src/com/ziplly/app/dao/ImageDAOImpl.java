package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.Image;

public class ImageDAOImpl extends BaseDAO implements ImageDAO {

	@Inject
	public ImageDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public Image save(Image image) {
		Preconditions.checkNotNull(image);
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(image);
		em.getTransaction().commit();
		return image;
	}

	@Override
	public Image findById(Long id) {
		Preconditions.checkNotNull(id);

		Query query =
		    getEntityManager().createQuery("from Image where id = :id").setParameter("id", id);
		Image image = (Image) query.getSingleResult();
		return image;
	}
}
