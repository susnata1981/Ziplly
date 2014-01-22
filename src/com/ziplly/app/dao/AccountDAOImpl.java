package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettings;

public class AccountDAOImpl implements AccountDAO {
	private Logger logger = Logger.getLogger(AccountDAOImpl.class.getCanonicalName());

	@Override
	public AccountDTO findByEmail(String email) throws NotFoundException {
		if (email == null) {
			logger.log(Level.ERROR, String.format("Invalid arguements to findByEmail %s", email));
			throw new IllegalArgumentException("Invalid arguement to findByEmail");
		}
		System.out.println("Looking for account with email:" + email);
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
	public AccountDTO findByEmailAndPassword(String email, String password)
			throws NotFoundException {
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
		em.getTransaction().begin();
		em.persist(account);

		for (PrivacySettings ps : account.getPrivacySettings()) {
			em.persist(ps);
		}

		for (AccountNotificationSettings ans : account.getNotificationSettings()) {
			em.persist(ans);
		}

		em.getTransaction().commit();
		AccountDTO result = EntityUtil.convert(account);
		em.close();
		return result;
	}

	@Override
	public AccountDTO update(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		AccountDTO result = null;
		try {
			em.merge(account);
		} catch (NoResultException ex) {
			throw new IllegalArgumentException();
		} finally {
			result = EntityUtil.convert(account);
			em.getTransaction().commit();
			em.close();
		}
		return result;
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

	@Deprecated
	@Override
	public List<AccountDTO> findAccountsByNeighborhood(EntityType entityType, Long neighborhoodId,
			int pageStart, int pageSize) {

		int start = pageStart;// * pageSize;
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createQuery("from Account a where type = :type and a.neighborhood.neighborhoodId = :neighborhoodId"
						+ " order by a.timeCreated");
		query.setParameter("type", entityType.getType());
		query.setParameter("neighborhoodId", neighborhoodId);
		query.setFirstResult(start).setMaxResults(pageSize);

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
			List<NeighborhoodDTO> neighborhoods, int start, int pageSize) {

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		List<Long> neighborhoodIds = getListOfNeighborhoodIds(neighborhoods);

		Query query = em
				.createQuery("from Account a where type = :type and a.neighborhood.neighborhoodId in :neighborhoodId"
						+ " order by a.timeCreated");
		query.setParameter("type", entityType.getType());
		query.setParameter("neighborhoodId", neighborhoodIds);
		query.setFirstResult(start).setMaxResults(pageSize);

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
	public Long getTotalAccountCountByNeighborhoods(EntityType entityType,
			List<NeighborhoodDTO> neighborhoods) {

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		List<Long> neighborhoodIds = getListOfNeighborhoodIds(neighborhoods);

		Query query = em
				.createNativeQuery("select count(n.neighborhood_id) from Account a, Neighborhood n where a.type = :type "
						+ "and a.neighborhood_id = n.neighborhood_id and  n.neighborhood_id in :neighborhoodIds");
		query.setParameter("type", entityType.getType());
		query.setParameter("neighborhoodIds", neighborhoodIds);

		BigInteger count = (BigInteger) query.getSingleResult();
		em.close();
		return count.longValue();
	}

	@Override
	public void updatePassword(Account account) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em
				.createNativeQuery("update Account set password = :password where account_id = :accountId");
		query.setParameter("password", account.getPassword());
		query.setParameter("accountId", account.getAccountId());
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();
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
	public Long findTotalAccountsByNeighborhood(EntityType type, Long neighborhoodId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createNativeQuery("select count(*) from account a, neighborhood n where a.neighborhood_id = n.neighborhood_id"
						+ " and a.type = :type and n.neighborhood_id = :neighborhood_id");
		query.setParameter("type", type.getType());
		query.setParameter("neighborhood_id", neighborhoodId);
		BigInteger count = (BigInteger) query.getSingleResult();
		em.close();
		return count.longValue();
	}

	private List<Long> getListOfNeighborhoodIds(List<NeighborhoodDTO> neighborhoods) {
		List<Long> neighborhoodIds = Lists.transform(neighborhoods,
				new Function<NeighborhoodDTO, Long>() {

					@Override
					public Long apply(NeighborhoodDTO n) {
						return n.getNeighborhoodId();
					}
				});
		return neighborhoodIds;
	}

	/**
	 * Finds personal account by gender.
	 */
	@Override
	public List<PersonalAccountDTO> findPersonalAccounts(Gender gender, long neighborhoodId, int start, int pageSize) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query;
		if (gender == Gender.ALL) {
			query = em.createQuery("from Account a where type = :type and a.neighborhood.neighborhoodId = :neighborhoodId");
		} else {
			query = em.createQuery("from Account a where type = :type and gender = :gender and a.neighborhood.neighborhoodId = :neighborhoodId");
			query.setParameter("gender", gender.name());
		}
		
		query.setParameter("type", StringConstants.PERSONAL_ACCOUNT_DISCRIMINATOR)
			.setParameter("neighborhoodId", neighborhoodId)
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

	//
	// TODO: Handles Gender.ALL in a hacky fashion.
	//
	@Override
	public Long getTotalPersonalAccountCountByGender(
			Gender gender,
			Long neighborhoodId) {
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query;
		
		if (gender == Gender.ALL) {
			query = em.createNativeQuery("select count(*) from account a, neighborhood n where a.neighborhood_id = n.neighborhood_id"
					+ " and a.type = :type and n.neighborhood_id = :neighborhood_id");
		} else {
			query = em.createNativeQuery("select count(*) from account a, neighborhood n where a.neighborhood_id = n.neighborhood_id"
					+ " and a.type = :type and a.gender = :gender and n.neighborhood_id = :neighborhood_id");
			query.setParameter("gender", gender.name());
		}
		
		query.setParameter("type", StringConstants.PERSONAL_ACCOUNT_DISCRIMINATOR)
		    .setParameter("neighborhood_id", neighborhoodId);
		
		BigInteger count = (BigInteger) query.getSingleResult();
		em.close();
		return count.longValue();
	}
	
}
