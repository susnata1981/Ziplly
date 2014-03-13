package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.ziplly.app.model.Image;

public class ImageDAOImpl implements ImageDAO {

	@Override
	public Image save(Image image) {
		Preconditions.checkNotNull(image);
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(image);
			em.getTransaction().commit();
			return image;
		} finally {
			em.close();
		}
	}

	@Override
	public Image findById(Long id) {
		Preconditions.checkNotNull(id);

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery("from Image where id = :id").setParameter("id", id);
			Image image = (Image) query.getSingleResult();
			return image;
		} finally {
			em.close();
		}
	}
}
