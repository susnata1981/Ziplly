package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.PostalCode;
import com.ziplly.app.model.PostalCodeDTO;

public class PostalCodeDAOImpl implements PostalCodeDAO {
	private Logger logger = Logger.getLogger(PostalCodeDAOImpl.class.getCanonicalName());

	@Inject
	public PostalCodeDAOImpl() {
	}

	@Override
	public PostalCodeDTO findById(Long neighborhoodId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findPostalCodeById");
		query.setParameter("neighborhoodId", neighborhoodId);
		try {
			PostalCode neighborhood = (PostalCode) query.getSingleResult();
			return EntityUtil.clone(neighborhood);
		} catch (NoResultException nre) {
			logger.warning(String.format("Couldn't find postal code for neighrborhood %d",
					neighborhoodId));
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	@Override
	public List<PostalCode> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			Query query = em.createNamedQuery("findAllPostalCodes");

			@SuppressWarnings("unchecked")
			List<PostalCode> result = query.getResultList();
			return result;
		} finally {
			em.close();
		}
	}

	@Override
	public List<PostalCodeDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("from PostalCode");
			@SuppressWarnings("unchecked")
			List<PostalCode> result = query.getResultList();
			return EntityUtil.clonePostalCodeList(result);
		} finally {
			em.close();
		}
	}

	@Override
	public Long findTotalPostalCodes(String countQuery) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery(countQuery);
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}
}
