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
import com.ziplly.app.model.NeighborhoodType;
import com.ziplly.app.model.PostalCode;

public class NeighborhoodDAOImpl implements NeighborhoodDAO {
	private Logger logger = Logger.getLogger(NeighborhoodDAOImpl.class.getCanonicalName());
	private PostalCodeDAO postalCodeDao;

	@Inject
	public NeighborhoodDAOImpl(PostalCodeDAO postalCodeDao) {
		this.postalCodeDao = postalCodeDao;
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
			logger.warn(String.format("Couldn't find neighborhood with id: %d", neighborhoodId));
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Neighborhood> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			Query query = em.createNamedQuery("findAllNeighborhoods");
			@SuppressWarnings("unchecked")
			List<Neighborhood> result = query.getResultList();
			return result;
		} finally {
			em.close();
		}
	}

	@Override
	public List<NeighborhoodDTO> findByPostalCode(String postalCode) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query = em.createQuery("select n from Neighborhood n join n.postalCodes p"
				+ " where p.postalCode = :postalCode and n.type is :type");
		query.setParameter("postalCode", postalCode)
		    .setParameter("type", NeighborhoodType.X.name());

		try {
			@SuppressWarnings("unchecked")
			List<Neighborhood> neighborhoods = (List<Neighborhood>) query.getResultList();
			List<NeighborhoodDTO> response = Lists.newArrayList();
			for (Neighborhood neighborhood : neighborhoods) {
				response.add(EntityUtil.clone(neighborhood));
			}
			return response;
		} catch (NoResultException nre) {
			logger.warn(String
					.format("Couldn't find neighborhood with postal code: %d", postalCode));
			return ImmutableList.of();
		} finally {
			em.close();
		}
	}

	@Override
	public List<NeighborhoodDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("from Neighborhood");
			@SuppressWarnings("unchecked")
			List<Neighborhood> result = query.getResultList();
			return EntityUtil.cloneNeighborhoodList(result);
		} finally {
			em.close();
		}
	}

	@Deprecated
	@Override
	public Long findTotalNeighborhoods(String countQuery) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery(countQuery);
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}

	@Override
	public List<NeighborhoodDTO> findAllDescendentNeighborhoods(Long neighborhoodId)
			throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		
		List<NeighborhoodDTO> result = Lists.newArrayList();
		
		try {
			Query query = em
					.createQuery("from Neighborhood n where n.parentNeighborhood.neighborhoodId = :neighborhoodId");
			query.setParameter("neighborhoodId", neighborhoodId);
			List<Neighborhood> neighborhoods = query.getResultList();
			for(Neighborhood neighborhood : neighborhoods) {
				result.add(EntityUtil.clone(neighborhood));
				
				List<NeighborhoodDTO> childNeighborhoods = findAllDescendentNeighborhoods(neighborhood.getNeighborhoodId());
				if (childNeighborhoods.size() == 0) {
					continue;
				}
				result.addAll(childNeighborhoods);
			}
			
			return result;
		} catch (NoResultException nre) {
			return ImmutableList.of();
		} finally {
			em.close();
		}
	}

	@Override
	public List<NeighborhoodDTO> findAllDescendentNeighborhoodsIncludingItself(Long neighborhoodId)
			throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		List<NeighborhoodDTO> result = Lists.newArrayList();
		
		try {
			Query query = em
					.createQuery("from Neighborhood n where n.parentNeighborhood.neighborhoodId = :neighborhoodId or neighborhoodId = :neighborhoodId");
			query.setParameter("neighborhoodId", neighborhoodId);
			List<Neighborhood> neighborhoods = query.getResultList();
			for(Neighborhood neighborhood : neighborhoods) {
				result.add(EntityUtil.clone(neighborhood));
				
				List<NeighborhoodDTO> childNeighborhoods = findAllDescendentNeighborhoods(neighborhood.getNeighborhoodId());
				if (childNeighborhoods.size() == 0) {
					continue;
				}
				result.addAll(childNeighborhoods);
			}
			
			return result;
		} catch (NoResultException nre) {
			return ImmutableList.of();
		} finally {
			em.close();
		}
	}

	@Override
	public void update(Neighborhood neighborhood) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
//			PostalCodeDTO postalCode = null;
//			try {
////				postalCode = postalCodeDao.findByPostalCode(neighborhood.getPostalCodes().iterator().next().getPostalCode());
//				String zip = neighborhood.getPostalCodes().iterator().next().getPostalCode();
//				Query query = em.createQuery("from PostalCode where postalCode = :postalCode")
//				    .setParameter("postalCode", zip);
//				PostalCode p = (PostalCode) query.getSingleResult();
//				neighborhood.addPostalCode(p);
//			} catch(NoResultException nre) {
////				System.out.println("Didn't find postal code");
////				postalCode = new PostalCodeDTO();
////				postalCode.setPostalCode(neighborhood.getPostalCode().getPostalCode());
//				throw new RuntimeException("Postal code not found.");
//			}
////			neighborhood.addPostalCode(new PostalCode(postalCode));
			em.merge(neighborhood);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
	
//	@Override
//	public void save(Neighborhood neighborhood) {
//		EntityManager em = EntityManagerService.getInstance().getEntityManager();
//
//		try {
//			em.getTransaction().begin();
//			PostalCode postalCode = null;
//			
//			// parent neighborhood
//			try {
//				if (neighborhood.getParentNeighborhood() != null) {
//					Long parentNeighborhoodId = neighborhood.getParentNeighborhood().getNeighborhoodId();
//					Query query = em.createQuery("from Neighborhood where neighborhoodId = :neighborhoodId");
//					query.setParameter("neighborhoodId", parentNeighborhoodId);
//					Neighborhood parent = (Neighborhood) query.getSingleResult();
//					neighborhood.setParentNeighborhood(parent);
//				}
//			} catch(NoResultException nre) {
//				throw nre;
//			}
//			
//			// postal code
//			try {
//				Query query = em.createQuery("from PostalCode where postalCode = :postalCode");
//				query.setParameter("postalCode", neighborhood.getPostalCode().getPostalCode());
//				postalCode = (PostalCode) query.getSingleResult();
//			} catch(NoResultException nre) {
//				postalCode = new PostalCode();
//				postalCode.setPostalCode(neighborhood.getPostalCode().getPostalCode());
//			}
//			
//			neighborhood.setPostalCode(postalCode);
//			em.persist(neighborhood);
//			em.getTransaction().commit();
//		} finally {
//			em.close();
//		}
//	}

	@Override
	public void save(Neighborhood neighborhood) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			em.getTransaction().begin();
			PostalCode postalCode = null;
			
			// parent neighborhood
			try {
				if (neighborhood.getParentNeighborhood() != null) {
					Long parentNeighborhoodId = neighborhood.getParentNeighborhood().getNeighborhoodId();
					Query query = em.createQuery("from Neighborhood where neighborhoodId = :neighborhoodId");
					query.setParameter("neighborhoodId", parentNeighborhoodId);
					Neighborhood parent = (Neighborhood) query.getSingleResult();
					neighborhood.setParentNeighborhood(parent);
				}
			} catch(NoResultException nre) {
				throw nre;
			}
			
//			// postal code
//			try {
//				Query query = em.createQuery("from PostalCode where postalCode = :postalCode");
//				query.setParameter("postalCode", neighborhood.getPostalCode().getPostalCode());
//				postalCode = (PostalCode) query.getSingleResult();
//			} catch(NoResultException nre) {
//				postalCode = new PostalCode();
//				postalCode.setPostalCode(neighborhood.getPostalCode().getPostalCode());
//			}
//			
//			neighborhood.setPostalCode(postalCode);
			em.persist(neighborhood);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public void delete(Long neighborhoodId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("from Neighborhood where neighborhoodId = :neighborhoodId");
			query.setParameter("neighborhoodId", neighborhoodId);
			Neighborhood n = (Neighborhood) query.getSingleResult();
			em.remove(n);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
}
