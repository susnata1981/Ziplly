package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettings;

public class AccountDAOImpl implements AccountDAO {
	private Logger logger = Logger.getLogger(AccountDAOImpl.class.getCanonicalName());
	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public AccountDAOImpl(NeighborhoodDAO neighborhoodDao) {
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public AccountDTO findByEmail(String email) throws NotFoundException {
		if (email == null) {
			logger.log(Level.ERROR, String.format("Invalid arguements to findByEmail %s", email));
			throw new IllegalArgumentException("Invalid arguement to findByEmail");
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmail");
		query.setParameter("email", email);
		Account account = null;
		AccountDTO resp = null;
		try {
			account = (Account) query.getSingleResult();
			account.getTweets();
		} catch (NoResultException ex) {
			System.out.println("Didn't find account");
			throw new NotFoundException();
		} finally {
			if (account == null) {
				throw new NotFoundException();
			}
			resp = EntityUtil.convert(account);
			em.close();
		}
		return resp;
	}

	@Override
	public AccountDTO findByEmailAndPassword(String email, String password) throws NotFoundException {
		if (email == null || password == null) {
			logger.log(Level.ERROR, "Invalid arguements to findByEmailAndPassword");
			throw new IllegalArgumentException("Invalid arguements to findByEmailAndPassword");
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountByEmailAndPassword");
		query.setParameter("email", email);
		query.setParameter("password", password);
		AccountDTO account = null;
		try {
			Account acct = (Account) query.getSingleResult();
			acct.getTweets();
			account = EntityUtil.convert(acct);
		} catch (NoResultException ex) {
			throw new NotFoundException();
		} finally {
			em.close();
		}

		return account;
	}

	@Override
	public AccountDTO findById(Long accountId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAccountById");
		// Query query =
		// em.createQuery("from Account a join fetch a.tweets t where a.accountId = :accountId");
		query.setParameter("accountId", accountId);
		AccountDTO result;
		try {
			Account account = (Account) query.getSingleResult();
			// account.getTweets();
			result = EntityUtil.convert(account);
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
		return result;
	}

	@Override
	public AccountDTO save(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();

			// Notification Settings.
			for (AccountNotificationSettings as : account.getNotificationSettings()) {
				em.persist(as);
			}

			// Privacy Settings.
			for (PrivacySettings ps : account.getPrivacySettings()) {
				em.persist(ps);
			}

			em.persist(account);
			em.getTransaction().commit();
			AccountDTO result = EntityUtil.convert(account);
			return result;
		} finally {
			em.close();
		}
	}

	@Override
	public AccountDTO update(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(account);
			em.getTransaction().commit();
			return EntityUtil.convert(account);
		} catch (NoResultException ex) {
			throw new IllegalArgumentException();
		} finally {
			em.close();
		}
	}

	@Override
	public void updatePassword(Account account) {
		Preconditions.checkNotNull(account);
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query =
			    em
			        .createNativeQuery("update account set password = :password where account_id = :accountId");
			query.setParameter("accountId", account.getAccountId()).setParameter(
			    "password",
			    account.getPassword());
			em.getTransaction().begin();
			query.executeUpdate();
			em.getTransaction().commit();
		} catch (NoResultException ex) {
			throw new IllegalArgumentException();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Account> getAll(int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findAllAccounts");

		@SuppressWarnings("unchecked")
		List<Account> result = query.getResultList();
		em.close();
		return result;
	}

	@SuppressWarnings("unused")
	private Interest getInterest(Activity activity) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Interest where activity = :activity");
		query.setParameter("activity", activity);

		Interest result = (Interest) query.getSingleResult();
		em.close();
		return result;
	}

	@Override
	public List<PersonalAccountDTO> findByZip(int zip) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from PersonalAccount where zip = :zip");
		query.setParameter("zip", zip);

		@SuppressWarnings("unchecked")
		List<PersonalAccount> accounts = (List<PersonalAccount>) query.getResultList();
		List<PersonalAccountDTO> response = Lists.newArrayList();
		for (PersonalAccount pa : accounts) {
			response.add(EntityUtil.clone(pa, false));
		}
		em.close();
		return response;
	}

	@Deprecated
	@Override
	public List<AccountDTO> findAllAccountsByZip(int zip) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Account where zip = :zip");
		query.setParameter("zip", zip);

		@SuppressWarnings("unchecked")
		List<Account> accounts = (List<Account>) query.getResultList();
		List<AccountDTO> response = Lists.newArrayList();
		for (Account pa : accounts) {
			response.add(EntityUtil.convert(pa));
		}
		em.close();
		return response;
	}

	@Override
	public List<AccountDTO> findAccountsByNeighborhood(EntityType entityType,
	    Long neighborhoodId,
	    int pageStart,
	    int pageSize) {

		int start = pageStart;// * pageSize;
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query =
		    em
		        .createQuery("select a from Account a join a.locations l where a.class = :type and l.neighborhood.neighborhoodId = :neighborhoodId"
		            + " order by a.timeCreated");

		query
		    .setParameter("type", entityType.getType())
		    .setParameter("neighborhoodId", neighborhoodId)
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<AccountDTO> response = Lists.newArrayList();
		try {
			@SuppressWarnings("unchecked")
			List<Account> accounts = (List<Account>) query.getResultList();
			for (Account pa : accounts) {
				response.add(EntityUtil.convert(pa));
			}
			return response;
		} catch (NoResultException nre) {
			return response;
		} finally {
			em.close();
		}
	}

	@Override
	public Collection<? extends AccountDTO> findAccountsByNeighborhoods(EntityType entityType,
	    List<Long> neighborhoodIds,
	    int start,
	    int pageSize) {

		if (neighborhoodIds.size() == 0) {
			return Collections.emptyList();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query =
		    em.createQuery("select distinct a from Account a join a.locations l where a.class = :type "
		        + "and l.neighborhood.neighborhoodId " + "in (:neighborhoodId) order by a.timeCreated");

		query
		    .setParameter("type", entityType.getType())
		    .setParameter("neighborhoodId", neighborhoodIds)
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<AccountDTO> response = Lists.newArrayList();
		try {
			@SuppressWarnings("unchecked")
			List<? extends Account> accounts = (List<? extends Account>) query.getResultList();

			for (Account pa : accounts) {
				response.add(EntityUtil.convert(pa));
			}
			return response;
		} catch (NoResultException nre) {
			return response;
		} finally {
			em.close();
		}
	}

	@Override
	public Long
	    getTotalAccountCountByNeighborhoods(EntityType entityType, List<Long> neighborhoodIds) {

		if (neighborhoodIds.size() == 0) {
			return 0L;
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query =
		    em
		        .createNativeQuery("select count(distinct a.account_id) from account a, account_location al, location l, neighborhood n "
		            + " where a.account_id = al.account_id and al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
		            + " and a.type = :type and n.neighborhood_id in (:neighborhoodIds)");

		query.setParameter("type", entityType.getType());
		query.setParameter("neighborhoodIds", neighborhoodIds);

		BigInteger count = (BigInteger) query.getSingleResult();
		em.close();
		return count.longValue();
	}

	@Override
	public List<AccountDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Account");
		@SuppressWarnings("unchecked")
		List<Account> result = query.getResultList();
		em.close();
		return EntityUtil.cloneAccountList(result);
	}

	@Override
	public List<AccountDTO> findAccounts(String queryStr, int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(queryStr);
		query.setFirstResult(start);
		query.setMaxResults(end - start);
		@SuppressWarnings("unchecked")
		List<Account> result = query.getResultList();
		List<AccountDTO> resp = EntityUtil.cloneAccountList(result);
		em.close();
		return resp;
	}

	@Override
	public Long findTotalAccounts(String countQuery) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(countQuery);
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}

	@Override
	public Long
	    findTotalAccountsByNeighborhood(EntityType type, Long neighborhoodId) throws NotFoundException {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			List<NeighborhoodDTO> neighborhoods =
			    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
			List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
			List<Long> neighborhoodIds =
			    Lists.transform(neighborhoods, new Function<NeighborhoodDTO, Long>() {

				    @Override
				    public Long apply(NeighborhoodDTO n) {
					    return n.getNeighborhoodId();
				    }
			    });

			allNeighborhoodIds.addAll(neighborhoodIds);

			Query query =
			    em
			        .createNativeQuery("select distinct count(a.account_id) from account a, account_location al, location l, neighborhood n "
			            + " where al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
			            + " and al.account_id = a.account_id and a.type = :type and n.neighborhood_id in (:neighborhood_ids)");

			query.setParameter("type", type.getType());
			query.setParameter("neighborhood_ids", allNeighborhoodIds);
			BigInteger count = (BigInteger) query.getSingleResult();
			return count.longValue();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	/**
	 * Finds personal account by gender.
	 * 
	 * @throws NotFoundException
	 */
	@Override
	public List<PersonalAccountDTO> findPersonalAccounts(Gender gender,
	    long neighborhoodId,
	    int start,
	    int pageSize) throws NotFoundException {

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query;

		List<NeighborhoodDTO> neighborhoods =
		    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
		List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
		List<Long> neighborhoodIdList = getNeighborhoodIds(neighborhoods);
		allNeighborhoodIds.addAll(neighborhoodIdList);

		if (gender == Gender.ALL) {
			query =
			    em
			        .createQuery("select a from Account a join a.locations l "
			            + " where a.class = PersonalAccount and l.neighborhood.neighborhoodId in (:neighborhoodId)");
		} else {
			query =
			    em.createQuery("select a from Account a join a.locations l "
			        + " where a.class = PersonalAccount and gender = :gender and "
			        + " l.neighborhood.neighborhoodId in (:neighborhoodId)");
			query.setParameter("gender", gender.name());
		}

		query
		    .setParameter("neighborhoodId", allNeighborhoodIds)
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<PersonalAccountDTO> result = Lists.newArrayList();
		try {
			@SuppressWarnings("unchecked")
			List<PersonalAccount> paList = query.getResultList();
			for (PersonalAccount pa : paList) {
				result.add(EntityUtil.clone(pa, false));
			}
			return result;
		} catch (NoResultException nre) {
			return result;
		} finally {
			em.close();
		}
	}

	@Override
	public List<BusinessAccountDTO>
	    findBusinessAccounts(long neighborhoodId, int start, int pageSize) throws NotFoundException {

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query;

		List<NeighborhoodDTO> neighborhoods =
		    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
		List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
		List<Long> neighborhoodIdList = getNeighborhoodIds(neighborhoods);
		allNeighborhoodIds.addAll(neighborhoodIdList);

		query =
		    em
		        .createQuery("select a from Account a join a.locations l "
		            + " where a.class = BusinessAccount and l.neighborhood.neighborhoodId in (:neighborhoodId)");
		query
		    .setParameter("neighborhoodId", allNeighborhoodIds)
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<BusinessAccountDTO> result = Lists.newArrayList();
		try {
			@SuppressWarnings("unchecked")
			List<BusinessAccount> baList = query.getResultList();
			for (BusinessAccount ba : baList) {
				result.add(EntityUtil.clone(ba, false));
			}
			return result;
		} catch (NoResultException nre) {
			return result;
		} finally {
			em.close();
		}
	}

	//
	// TODO: Handles Gender.ALL in a hacky fashion.
	//
	@Override
	public Long getTotalPersonalAccountCountByGender(Gender gender, Long neighborhoodId) {

		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			List<NeighborhoodDTO> neighborhoods =
			    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
			List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
			List<Long> neighborhoodIdList = getNeighborhoodIds(neighborhoods);
			allNeighborhoodIds.addAll(neighborhoodIdList);
			Query query;

			if (gender == Gender.ALL) {
				query =
				    em
				        .createNativeQuery("select count(distinct a.account_id) from account a, account_location al, location l, neighborhood n "
				            + "where a.account_id = al.account_id and al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
				            + " and a.type = :type and n.neighborhood_id in (:neighborhoodIds)");
			} else {
				query =
				    em
				        .createNativeQuery("select count(distinct a.account_id) from account a, account_location al, location l, neighborhood n "
				            + "where a.account_id = al.account_id and al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
				            + " and a.type = :type and a.gender = :gender and n.neighborhood_id in (:neighborhoodIds)");
				query.setParameter("gender", gender.name());
			}

			query.setParameter("type", StringConstants.PERSONAL_ACCOUNT_DISCRIMINATOR).setParameter(
			    "neighborhoodIds",
			    allNeighborhoodIds);

			BigInteger count = (BigInteger) query.getSingleResult();
			return count.longValue();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			em.close();
		}
		return 0L;
	}

	private List<Long> getNeighborhoodIds(List<NeighborhoodDTO> neighborhoods) {
		if (neighborhoods.size() == 0) {
			return Collections.emptyList();
		}

		List<Long> neighborhoodIdList =
		    Lists.transform(neighborhoods, new Function<NeighborhoodDTO, Long>() {
			    @Override
			    public Long apply(NeighborhoodDTO neighborhood) {
				    return neighborhood.getNeighborhoodId();
			    }
		    });

		return neighborhoodIdList;
	}
}
