package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.PostalCode;
import com.ziplly.app.model.PostalCodeDTO;

public class PostalCodeDAOImpl extends BaseDAO implements PostalCodeDAO {
	private Logger logger = Logger.getLogger(PostalCodeDAOImpl.class.getCanonicalName());

	@Inject
	public PostalCodeDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public PostalCodeDTO findById(Long neighborhoodId) throws NotFoundException {

		Query query = getEntityManager().createNamedQuery("findPostalCodeById");
		query.setParameter("neighborhoodId", neighborhoodId);
		try {
			PostalCode neighborhood = (PostalCode) query.getSingleResult();
			return EntityUtil.clone(neighborhood);
		} catch (NoResultException nre) {
			logger.warning(String
			    .format("Couldn't find postal code for neighrborhood %d", neighborhoodId));
			throw new NotFoundException();
		}
	}

	@Override
	public List<PostalCode> getAll(int start, int end) {
		Query query = getEntityManager().createNamedQuery("findAllPostalCodes");

		@SuppressWarnings("unchecked")
		List<PostalCode> result = query.getResultList();
		return result;
	}

	@Override
	public List<PostalCodeDTO> findAll() {
		getEntityManager().getTransaction().begin();
		Query query = getEntityManager().createQuery("from PostalCode");
		@SuppressWarnings("unchecked")
		List<PostalCode> result = query.getResultList();
		return EntityUtil.clonePostalCodeList(result);
	}

	@Override
	public Long findTotalPostalCodes(String countQuery) {
		Query query = getEntityManager().createQuery(countQuery);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	@Override
	public PostalCodeDTO findByPostalCode(String postalCode) {
		Query query = getEntityManager().createQuery("from PostalCode where postalCode = :postalCode");
		query.setParameter("postalCode", postalCode);
		PostalCode result = (PostalCode) query.getSingleResult();
		return EntityUtil.clone(result);
	}
}
