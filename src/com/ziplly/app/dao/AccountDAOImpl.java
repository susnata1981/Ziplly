package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.BusinessAccount;
import com.ziplly.app.server.model.jpa.Location;
import com.ziplly.app.server.model.jpa.Neighborhood;
import com.ziplly.app.server.model.jpa.PersonalAccount;

public class AccountDAOImpl extends BaseDAO implements AccountDAO {
	private static final Object PERSONAL_ACCOUNT_DISCRIMINATOR = "personal";
  private Logger logger = Logger.getLogger(AccountDAOImpl.class.getCanonicalName());
	private NeighborhoodDAO neighborhoodDao;

	private static final Function<Neighborhood, Long> neighborhoodIdExtractor = new Function<Neighborhood, Long>() {

    @Override
    public Long apply(Neighborhood n) {
      return n.getNeighborhoodId();
    }
  };
	
	@Inject
	public AccountDAOImpl(
			Provider<EntityManager> entityManagerProvider,
	    NeighborhoodDAO neighborhoodDao) {
		super(entityManagerProvider);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public Account findByEmail(String email) throws NotFoundException {
		if (email == null) {
			throw new IllegalArgumentException("Invalid arguement to findByEmail");
		}

		Query query = getEntityManager().createNamedQuery("findAccountByEmail");
		query.setParameter("email", email);
		try {
			Account account = (Account) query.getSingleResult();
			account.getTweets();
			return account;
		} catch (NoResultException ex) {
			logger.info(String.format("Didn't find account with %s", email));
			throw new NotFoundException();
		} 
	}

	@Override
	public AccountDTO findByEmailAndPassword(String email, String password) throws NotFoundException {
		if (email == null || password == null) {
			logger.log(Level.ERROR, "Invalid arguements to findByEmailAndPassword");
			throw new IllegalArgumentException("Invalid arguements to findByEmailAndPassword");
		}

		Query query = getEntityManager().createNamedQuery("findAccountByEmailAndPassword");
		query.setParameter("email", email);
		query.setParameter("password", password);
		AccountDTO account = null;
		try {
			Account acct = (Account) query.getSingleResult();
			acct.getTweets();
			account = EntityUtil.convert(acct);
		} catch (NoResultException ex) {
			throw new NotFoundException();
		}

		return account;
	}

	@Override
	public Account findById(Long accountId) throws NotFoundException {

		Query query = getEntityManager().createNamedQuery("findAccountById");
		query.setParameter("accountId", accountId);
		try {
			return (Account) query.getSingleResult();
			// account.getTweets();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}
	}

	@Transactional
	@Override
	public Account save(Account account) {
		EntityManager em = getEntityManager();
		em.persist(account);
		return account;
	}

	@Transactional
	@Override
	public Account update(Account account) {
		EntityManager em = getEntityManager();
		Account updatedAccount = em.merge(account);
		return updatedAccount;
	}

	@Transactional
	@Override
	public void updatePassword(Account account) {
		Preconditions.checkNotNull(account);
		EntityManager em = getEntityManager(); 
		try {
			Query query =
			    em
			        .createNativeQuery("update account set password = :password where account_id = :accountId");
			query.setParameter("accountId", account.getAccountId()).setParameter(
			    "password",
			    account.getPassword());
			query.executeUpdate();
		} catch (NoResultException ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public List<Account> getAll(int start, int end) {
		Query query = getEntityManager().createNamedQuery("findAllAccounts");

		@SuppressWarnings("unchecked")
		List<Account> result = query.getResultList();
		return result;
	}

	@Override
	public List<PersonalAccountDTO> findByZip(int zip) {
		Query query = getEntityManager().createQuery("from PersonalAccount where zip = :zip");
		query.setParameter("zip", zip);

		@SuppressWarnings("unchecked")
		List<PersonalAccount> accounts = (List<PersonalAccount>) query.getResultList();
		List<PersonalAccountDTO> response = Lists.newArrayList();
		for (PersonalAccount pa : accounts) {
			response.add(EntityUtil.clone(pa, false));
		}
		return response;
	}

	@Override
	public List<Account> findAccountsByNeighborhood(
			EntityType entityType,
	    Long neighborhoodId,
	    int pageStart,
	    int pageSize) {

		int start = pageStart;// * pageSize;
		EntityManager em = getEntityManager();
		Query query =
		    em.createQuery("select a from Account a join a.locations l where a.class = :type and"
		        + " l.neighborhood.neighborhoodId = :neighborhoodId and a.status = :status order by a.timeCreated desc");
		query
		    .setParameter("type", entityType.getType())
		    .setParameter("neighborhoodId", neighborhoodId)
		    .setParameter("status", AccountStatus.ACTIVE.name())
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		try {
			@SuppressWarnings("unchecked")
			List<Account> accounts = (List<Account>) query.getResultList();
			
			// Filter accounts
			accounts = filterBlacklistedAccounts(accounts);
			
//			for (Account pa : accounts) {
//				response.add(EntityUtil.convert(pa));
//			}
			return accounts;
		} catch (NoResultException nre) {
			return ImmutableList.<Account>of();
		}
	}

	@Override
	public Collection<? extends AccountDTO> findAccountsByNeighborhoods(
			EntityType entityType,
	    List<Long> neighborhoodIds,
	    int start,
	    int pageSize) {

		if (neighborhoodIds.size() == 0) {
			return Collections.emptyList();
		}

		EntityManager em = getEntityManager();

		Query query =
		    em.createQuery("select distinct a from Account a join a.locations l where a.class = :type "
		        + "and l.neighborhood.neighborhoodId in (:neighborhoodId) and a.status = :status order by a.timeCreated desc");

		query
		    .setParameter("type", entityType.getType())
		    .setParameter("neighborhoodId", neighborhoodIds)
		    .setParameter("status", AccountStatus.ACTIVE.name())
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<AccountDTO> response = Lists.newArrayList();
		try {
			@SuppressWarnings("unchecked")
			List<? extends Account> accounts = (List<? extends Account>) query.getResultList();

			// Filter accounts
      accounts = filterBlacklistedAccounts(accounts);
      
			for (Account pa : accounts) {
				response.add(EntityUtil.convert(pa));
			}
			return response;
		} catch (NoResultException nre) {
			return response;
		}
	}

	@Override
	public Long getTotalAccountCountByNeighborhoods(EntityType entityType, List<Long> neighborhoodIds) {
		if (neighborhoodIds.size() == 0) {
			return 0L;
		}

		EntityManager em = getEntityManager();
		Query query =
		    em.createNativeQuery("select count(distinct a.account_id) from account a, account_location al, location l, neighborhood n "
		            + " where a.account_id = al.account_id and al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
		            + " and a.type = :type and n.neighborhood_id in (:neighborhoodIds) and a.status = :status and "
		            + "a.account_id not in (:blackList)");

		query.setParameter("type", entityType.getType())
		  .setParameter("neighborhoodIds", neighborhoodIds)
		  .setParameter("status", AccountStatus.ACTIVE.name())
		  .setParameter("blackList", getBlackListedAccounts());
		
		BigInteger count = (BigInteger) query.getSingleResult();
		return count.longValue();
	}

	@Override
	public Long
	    findTotalAccountsByNeighborhood(EntityType type, Long neighborhoodId) throws NotFoundException {
		EntityManager em = getEntityManager();

		try {
			List<Neighborhood> neighborhoods =
			    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
			List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
			List<Long> neighborhoodIds =
			    Lists.transform(neighborhoods, new Function<Neighborhood, Long>() {

				    @Override
				    public Long apply(Neighborhood n) {
					    return n.getNeighborhoodId();
				    }
			    });

			allNeighborhoodIds.addAll(neighborhoodIds);

			Query query =
			    em.createNativeQuery("select distinct count(a.account_id) from account a, account_location al, location l, "
			            + " neighborhood n where al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
			            + " and al.account_id = a.account_id and a.type = :type and n.neighborhood_id in (:neighborhood_ids)"
			            + " and a.status = :status and a.account_id not in (:blackList)");

			query.setParameter("type", type.getType())
			  .setParameter("neighborhood_ids", allNeighborhoodIds)
			  .setParameter("status", AccountStatus.ACTIVE.name())
			  .setParameter("blackList", getBlackListedAccounts());
			  
			BigInteger count = (BigInteger) query.getSingleResult();
			return count.longValue();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}
	}

	/**
	 * Finds personal accounts by gender.
	 */
	@Override
	public List<PersonalAccountDTO> findPersonalAccounts(
			Gender gender,
	    long neighborhoodId,
	    int start,
	    int pageSize) throws NotFoundException {

		EntityManager em = getEntityManager();
		Query query;

		List<Neighborhood> neighborhoods =
		    neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
		List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
		List<Long> neighborhoodIdList = getNeighborhoodIds(neighborhoods);
		allNeighborhoodIds.addAll(neighborhoodIdList);

		if (gender == Gender.ALL) {
			query =
			    em.createQuery("select a from Account a join a.locations l "
			            + " where a.class = PersonalAccount and l.neighborhood.neighborhoodId in (:neighborhoodId)"
			            + " and a.status = :status order by a.timeCreated desc");
		} else {
			query =
			    em.createQuery("select a from Account a join a.locations l "
			        + " where a.class = PersonalAccount and gender = :gender and a.status = :status and"
			        + " l.neighborhood.neighborhoodId in (:neighborhoodId) order by a.timeCreated desc");
			query.setParameter("gender", gender.name());
			    
		}

		query
		    .setParameter("neighborhoodId", allNeighborhoodIds)
		    .setParameter("status", AccountStatus.ACTIVE.name())
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
		}
	}

	//
	// TODO: Handles Gender.ALL in a hacky fashion.
	//
	@Override
	public Long getTotalPersonalAccountCountByGender(Gender gender, Long neighborhoodId) {
		EntityManager em = getEntityManager();

		try {
			List<Neighborhood> neighborhoods =
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
				            + " and a.type = :type and a.status = :status and n.neighborhood_id in (:neighborhoodIds)");
			} else {
				query =
				    em
				        .createNativeQuery("select count(distinct a.account_id) from account a, account_location al, location l, neighborhood n "
				            + "where a.account_id = al.account_id and al.location_id = l.location_id and l.neighborhood_id = n.neighborhood_id"
				            + " and a.type = :type and a.gender = :gender and a.status = :status and n.neighborhood_id in (:neighborhoodIds)");
				query.setParameter("gender", gender.name());
			}

			query.setParameter("type", PERSONAL_ACCOUNT_DISCRIMINATOR)
			    .setParameter("neighborhoodIds", allNeighborhoodIds)
			    .setParameter("status", AccountStatus.ACTIVE.name());

			BigInteger count = (BigInteger) query.getSingleResult();
			return count.longValue();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0L;
	}

	private List<Long> getNeighborhoodIds(List<Neighborhood> neighborhoods) {
		if (neighborhoods.size() == 0) {
			return Collections.emptyList();
		}

		List<Long> neighborhoodIdList = Lists.transform(neighborhoods, neighborhoodIdExtractor);

		return neighborhoodIdList;
	}

	/**
	 * Only called by ADMINISTRATOR to change locations.
	 */
	@Transactional
	@Override
	public void updateLocation(AccountDTO account, LocationDTO location) throws NotFoundException {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("from Account where accountId = :accountId");
		query.setParameter("accountId", account.getAccountId());
		Account acct = (Account) query.getSingleResult();
		Location loc = acct.getLocations().iterator().next();
		loc.setNeighborhood(new Neighborhood(location.getNeighborhood()));
		em.merge(loc);
	}

	@Override
	public List<AccountDTO> getAccountCreatedWithin(
			int daysLookback,
	    long neighborhoodId,
	    EntityType type) {

		EntityManager em = getEntityManager();
		DateTime timeCreated = new DateTime();
		Date startingDate = timeCreated.minusDays(daysLookback).toDate();
		Query query =
		    em.createQuery("select a from Account a join a.locations al, Neighborhood n where a.class = :class and "
		            + "al.neighborhood.neighborhoodId = n.neighborhoodId and n.neighborhoodId = :neighborhoodId and "
		            + "a.status = :status and a.timeCreated >= :timeCreated order by a.timeCreated desc");

		query
		    .setParameter("timeCreated", startingDate)
		    .setParameter("neighborhoodId", neighborhoodId)
		    .setParameter("class", type.getType())
		    .setParameter("status", AccountStatus.ACTIVE.name());

		List<Account> accounts = query.getResultList();
		
		// Filter accounts
    accounts = filterBlacklistedAccounts(accounts);
    
		return EntityUtil.cloneAccountList(accounts);
	}
	
	@Override
  public List<AccountDTO> findAll() {
    EntityManager em = getEntityManager();
    Query query = em.createQuery("from Account");
    @SuppressWarnings("unchecked")
    List<Account> result = query.getResultList();
    return EntityUtil.cloneAccountList(result);
  }

  @Deprecated
  @Override
  public List<AccountDTO> findAccounts(String queryStr, int start, int end) {
    EntityManager em = getEntityManager();
    Query query = em.createQuery(queryStr);
    query.setFirstResult(start);
    query.setMaxResults(end - start);
    @SuppressWarnings("unchecked")
    List<Account> result = query.getResultList();
    List<AccountDTO> resp = EntityUtil.cloneAccountList(result);
    return resp;
  }

  @Deprecated
  @Override
  public Long findTotalAccounts(String countQuery) {
    EntityManager em = getEntityManager();
    Query query = em.createQuery(countQuery);
    Long count = (Long) query.getSingleResult();
    return count;
  }
  
  @Deprecated
  @Override
  public List<AccountDTO> findAllAccountsByZip(int zip) {
    EntityManager em = getEntityManager();
    Query query = em.createQuery("from Account where zip = :zip");
    query.setParameter("zip", zip);

    @SuppressWarnings("unchecked")
    List<Account> accounts = (List<Account>) query.getResultList();
    List<AccountDTO> response = Lists.newArrayList();
    for (Account pa : accounts) {
      response.add(EntityUtil.convert(pa));
    }
    return response;
  }
  
  @Deprecated
  @Override
  public List<BusinessAccountDTO>
      findBusinessAccounts(long neighborhoodId, int start, int pageSize) throws NotFoundException {

    Query query;

    List<Neighborhood> neighborhoods =
        neighborhoodDao.findAllDescendentNeighborhoods(neighborhoodId);
    List<Long> allNeighborhoodIds = Lists.newArrayList(neighborhoodId);
    List<Long> neighborhoodIdList = getNeighborhoodIds(neighborhoods);
    allNeighborhoodIds.addAll(neighborhoodIdList);

    query = getEntityManager().createQuery("select a from Account a join a.locations l "
                + " where a.class = BusinessAccount and l.neighborhood.neighborhoodId in (:neighborhoodId)"
                + " order by a.timeCreated desc");
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
    }
  }
}
