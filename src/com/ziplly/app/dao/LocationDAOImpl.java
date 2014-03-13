package com.ziplly.app.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Location;

public class LocationDAOImpl implements LocationDAO {
	private Logger logger = Logger.getLogger(LocationDAOImpl.class.getCanonicalName());

	@Override
	public Location findById(Long locationId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query = em.createQuery("from Location where locationId = :locationId");
		query.setParameter("locationId", locationId);

		try {
			Location result = (Location) query.getSingleResult();
			return result;
		} catch (NoResultException nre) {
			String error = String.format("Couldn't find location %d", locationId);
			logger.warning(error);
			throw new NotFoundException(error);
		} finally {
			em.close();
		}
	}

}
