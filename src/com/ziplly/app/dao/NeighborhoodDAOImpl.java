package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;

public class NeighborhoodDAOImpl implements NeighborhoodDAO {
	private Logger logger = Logger.getLogger(NeighborhoodDAOImpl.class.getCanonicalName());

	@Inject
	public NeighborhoodDAOImpl() {
	}

	@Override
	public NeighborhoodDTO findById(Long neighborhoodId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findNeighborhoodById");
		query.setParameter("neighborhoodId", neighborhoodId);
		try {
			Neighborhood neighborhood = (Neighborhood) query.getSingleResult();
			return EntityUtil.clone(neighborhood);
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Neighborhood> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAllNeighborhoods");

		@SuppressWarnings("unchecked")
		List<Neighborhood> result = query.getResultList();
		em.close();
		return result;
	}

	// @Override
	// public NeighborhoodDTO findFirstByPostalCode(int postalCode) {
	// return findByPostalCode(postalCode).get(0);
	// }

	@Override
	public List<NeighborhoodDTO> findByPostalCode(String postalCode) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query = em.createQuery("select n from Neighborhood n, PostalCode p "
				+ "where n.postalCode.postalCodeId = p.postalCodeId "
				+ "and p.postalCode = :postalCode");
		query.setParameter("postalCode", postalCode);

		try {
			@SuppressWarnings("unchecked")
			List<Neighborhood> neighborhoods = (List<Neighborhood>) query.getResultList();
			List<NeighborhoodDTO> response = Lists.newArrayList();
			for (Neighborhood neighborhood : neighborhoods) {
				response.add(EntityUtil.clone(neighborhood));
			}
			return response;
		} catch (NoResultException nre) {
			return ImmutableList.of();
		} finally {
			em.close();
		}

	}

	@Override
	public List<NeighborhoodDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Neighborhood");
		@SuppressWarnings("unchecked")
		List<Neighborhood> result = query.getResultList();
		em.close();
		return EntityUtil.cloneNeighborhoodList(result);
	}

	@Override
	public Long findTotalNeighborhoods(String countQuery) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(countQuery);
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}
}
