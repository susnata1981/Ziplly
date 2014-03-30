package com.ziplly.app.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NeighborhoodType;
import com.ziplly.app.model.PostalCode;

public class NeighborhoodDAOImpl extends BaseDAO implements NeighborhoodDAO {
	private Logger logger = Logger.getLogger(NeighborhoodDAOImpl.class.getCanonicalName());

	@Inject
	public NeighborhoodDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public NeighborhoodDTO findById(Long neighborhoodId) throws NotFoundException {
		Query query = getEntityManager().createNamedQuery("findNeighborhoodById");
		query.setParameter("neighborhoodId", neighborhoodId);
		try {
			Neighborhood neighborhood = (Neighborhood) query.getSingleResult();
			return EntityUtil.clone(neighborhood);
		} catch (NoResultException nre) {
			logger.warn(String.format("Couldn't find neighborhood with id: %d", neighborhoodId));
			throw new NotFoundException();
		}
	}

	@Override
	public List<Neighborhood> getAll(int start, int end) {
		Query query = getEntityManager().createNamedQuery("findAllNeighborhoods");
		@SuppressWarnings("unchecked")
		List<Neighborhood> result = query.getResultList();
		return result;
	}

	@Transactional
	@Override
	public List<NeighborhoodDTO> findByPostalCode(String postalCode) {
		Query query =
		    getEntityManager().createQuery(
		        "select n from Neighborhood n join n.postalCodes p"
		            + " where p.postalCode = :postalCode and n.type is :type");
		query.setParameter("postalCode", postalCode).setParameter(
		    "type",
		    NeighborhoodType.NEIGHBORHOOD.name());

		try {
			@SuppressWarnings("unchecked")
			List<Neighborhood> neighborhoods = (List<Neighborhood>) query.getResultList();
			List<NeighborhoodDTO> response = Lists.newArrayList();
			for (Neighborhood neighborhood : neighborhoods) {
				response.add(EntityUtil.clone(neighborhood));
			}
			return response;
		} catch (NoResultException nre) {
			logger.warn(String.format("Couldn't find neighborhood with postal code: %d", postalCode));
			return ImmutableList.of();
		}
	}

	@Transactional
	@Override
	public List<NeighborhoodDTO> findAll() {
		Query query = getEntityManager().createQuery("from Neighborhood");
		@SuppressWarnings("unchecked")
		List<Neighborhood> result = query.getResultList();
		return EntityUtil.cloneNeighborhoodList(result);
	}

	@Deprecated
	@Override
	public Long findTotalNeighborhoods(String countQuery) {
		Query query = getEntityManager().createQuery(countQuery);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	@Transactional
	@Override
	public List<NeighborhoodDTO>
	    findAllDescendentNeighborhoods(Long neighborhoodId) throws NotFoundException {
		// EntityManager em = EntityManagerService.getInstance().getEntityManager();

		List<NeighborhoodDTO> result = Lists.newArrayList();
		try {
			Query query =
			    getEntityManager().createQuery(
			        "from Neighborhood n where n.parentNeighborhood.neighborhoodId = :neighborhoodId");
			query.setParameter("neighborhoodId", neighborhoodId);
			List<Neighborhood> neighborhoods = query.getResultList();
			for (Neighborhood neighborhood : neighborhoods) {
				result.add(EntityUtil.clone(neighborhood));

				List<NeighborhoodDTO> childNeighborhoods =
				    findAllDescendentNeighborhoods(neighborhood.getNeighborhoodId());
				if (childNeighborhoods.size() == 0) {
					continue;
				}
				result.addAll(childNeighborhoods);
			}

			return result;
		} catch (NoResultException nre) {
			return ImmutableList.of();
		}
	}

	@Transactional
	@Override
	public List<NeighborhoodDTO>
	    findAllDescendentNeighborhoodsIncludingItself(Long neighborhoodId) throws NotFoundException {

		EntityManager em = getEntityManager();
		List<NeighborhoodDTO> result = Lists.newArrayList();

		try {
			Query query =
			    em
			        .createQuery("from Neighborhood n where n.parentNeighborhood.neighborhoodId = :neighborhoodId or neighborhoodId = :neighborhoodId");
			query.setParameter("neighborhoodId", neighborhoodId);
			List<Neighborhood> neighborhoods = query.getResultList();
			for (Neighborhood neighborhood : neighborhoods) {
				result.add(EntityUtil.clone(neighborhood));

				List<NeighborhoodDTO> childNeighborhoods =
				    findAllDescendentNeighborhoods(neighborhood.getNeighborhoodId());
				if (childNeighborhoods.size() == 0) {
					continue;
				}
				result.addAll(childNeighborhoods);
			}

			return result;
		} catch (NoResultException nre) {
			return ImmutableList.of();
		}
	}

	@Transactional
	@Override
	public void update(Neighborhood neighborhood) {
		EntityManager em = getEntityManager();
		em.merge(neighborhood);
	}

	@Deprecated
	@Transactional
	@Override
	public void save(Neighborhood neighborhood) {
		EntityManager em = getEntityManager();

		try {
			// PostalCode postalCode = null;

			// parent neighborhood
			if (neighborhood.getParentNeighborhood() != null) {
				Long parentNeighborhoodId = neighborhood.getParentNeighborhood().getNeighborhoodId();
				Query query = em.createQuery("from Neighborhood where neighborhoodId = :neighborhoodId");
				query.setParameter("neighborhoodId", parentNeighborhoodId);
				Neighborhood parent = (Neighborhood) query.getSingleResult();
				neighborhood.setParentNeighborhood(parent);
			}
		} catch (NoResultException nre) {
			throw nre;
		}

		// // postal code
		// try {
		// Query query =
		// em.createQuery("from PostalCode where postalCode = :postalCode");
		// query.setParameter("postalCode",
		// neighborhood.getPostalCode().getPostalCode());
		// postalCode = (PostalCode) query.getSingleResult();
		// } catch(NoResultException nre) {
		// postalCode = new PostalCode();
		// postalCode.setPostalCode(neighborhood.getPostalCode().getPostalCode());
		// }
		//
		// neighborhood.setPostalCode(postalCode);
		em.persist(neighborhood);
	}

	@Transactional
	@Override
	public void delete(Long neighborhoodId) {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("from Neighborhood where neighborhoodId = :neighborhoodId");
		query.setParameter("neighborhoodId", neighborhoodId);
		Neighborhood n = (Neighborhood) query.getSingleResult();
		em.remove(n);
	}

	@Transactional
	@Override
	public NeighborhoodDTO findOrCreateNeighborhood(NeighborhoodDTO neighborhood) {
		Preconditions.checkNotNull(neighborhood);
		EntityManager em = getEntityManager();
		Neighborhood existingNeighborhood = null;

		try {
			Query query =
			    em
			        .createQuery("select n from Neighborhood n where lower(name) = :neighborhoodName and lower(n.city) = :city and type = :type");

			query
			    .setParameter("neighborhoodName", neighborhood.getName().toLowerCase())
			    .setParameter("city", neighborhood.getParentNeighborhood().getName().toLowerCase())
			    .setParameter("type", neighborhood.getType().name());

			existingNeighborhood = (Neighborhood) query.getSingleResult();
		} catch (NoResultException nre) {
			logger.info(String.format(
			    "Didn't find neighborhood %s, in postal code %s",
			    neighborhood.getName(),
			    neighborhood.getPostalCodes().get(0).getPostalCode()));
		}

		// We found a match!
		if (existingNeighborhood != null) {
			return EntityUtil.clone(existingNeighborhood);
		}

		return createNeighborhoodTree(neighborhood);
	}

	@Transactional
	@Override
	public List<NeighborhoodDTO> findNeighborhoodsByLocality(NeighborhoodDTO neighborhood) {
		Preconditions.checkNotNull(neighborhood);

		try {
			Query query =
			    getEntityManager().createQuery(
			        "from Neighborhood where city = :city and type = :type and state = :state");
			query
			    .setParameter("city", neighborhood.getCity())
			    .setParameter("type", NeighborhoodType.NEIGHBORHOOD.name())
			    .setParameter("state", neighborhood.getState());
			List<Neighborhood> neighborhoods = query.getResultList();
			return EntityUtil.cloneNeighborhoodList(neighborhoods);
		} catch (NoResultException nre) {
			return Collections.emptyList();
		}
	}

	private NeighborhoodDTO createNeighborhoodTree(NeighborhoodDTO neighborhood) {
		if (neighborhood == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		// em.getTransaction().begin();

		Neighborhood newNeighborhood = new Neighborhood(neighborhood);
		Neighborhood parent = newNeighborhood.getParentNeighborhood();
		Neighborhood current = newNeighborhood;

		while (parent != null) {
			Neighborhood parentNeighborhood = null;
			Query query = null;
			try {
				// Countries have "" in state field - so this is so we don't duplicate
				// countries.
				if (parent.getType() == NeighborhoodType.COUNTRY) {
					query = em.createQuery("from Neighborhood where lower(name) = :name");
					query.setParameter("name", parent.getName().toLowerCase());
				} else {
					query =
					    em
					        .createQuery("from Neighborhood where lower(name) = :name and lower(state) = :state and type = :type");
					query
					    .setParameter("name", parent.getName().toLowerCase())
					    .setParameter("state", parent.getState())
					    .setParameter("type", parent.getType().name());
				}
				parentNeighborhood = (Neighborhood) query.getSingleResult();
			} catch (NoResultException nre) {
				// break;
			}

			if (parentNeighborhood != null) {
				current.setParentNeighborhood(parentNeighborhood);
			}

			current = parent;
			parent = parent.getParentNeighborhood();
		}

		Query query = em.createQuery("from PostalCode where zip = :zip");
		query.setParameter("zip", newNeighborhood.getPostalCodes().iterator().next().getPostalCode());
		try {
			PostalCode pc = (PostalCode) query.getSingleResult();
			newNeighborhood.getPostalCodes().clear();
			newNeighborhood.addPostalCode(pc);
		} catch (NoResultException nre) {
			// postal code doesn't exist.
		}

		em.persist(newNeighborhood);
		// em.getTransaction().commit();
		return EntityUtil.clone(newNeighborhood);
	}
}
