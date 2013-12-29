package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;

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
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findPostalCodeById");
		query.setParameter("neighborhoodId", neighborhoodId);
		try {
			PostalCode neighborhood = (PostalCode) query.getSingleResult();
			return EntityUtil.clone(neighborhood);
		} catch(NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	@Override
	public List<PostalCode> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findAllPostalCodes");
		
		@SuppressWarnings("unchecked")
		List<PostalCode> result = query.getResultList();
		em.close();
		return result;
	}

	@Override
	public List<PostalCodeDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from PostalCode");
		@SuppressWarnings("unchecked")
		List<PostalCode> result = query.getResultList();
		em.close();
		return EntityUtil.clonePostalCodeList(result);
	}

	@Override
	public Long findTotalPostalCodes(String countQuery) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(countQuery);
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}
}

