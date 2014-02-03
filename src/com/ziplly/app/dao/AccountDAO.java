package com.ziplly.app.dao;

import java.util.Collection;
import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public interface AccountDAO {
	AccountDTO findByEmail(String email) throws NotFoundException;
	AccountDTO findByEmailAndPassword(String email, String password) throws NotFoundException;
	AccountDTO findById(Long accountId) throws NotFoundException;
	AccountDTO save(Account user);
	void updatePassword(Account account);
	AccountDTO update(Account user);
	List<Account> getAll(int start, int end);
	List<PersonalAccountDTO> findByZip(int zip);
	List<AccountDTO> findAll();
	List<AccountDTO> findAccounts(String query, int start, int end);
	Long findTotalAccounts(String countQuery);
	List<AccountDTO> findAllAccountsByZip(int zip);
	List<AccountDTO> findAccountsByNeighborhood(EntityType entityType, Long neighborhoodId, int pageStart, int pageSize);
	Long findTotalAccountsByNeighborhood(EntityType type, Long neighborhoodId);
	Long getTotalAccountCountByNeighborhoods(EntityType entityType, List<NeighborhoodDTO> neighborhoodIds);
	Collection<? extends AccountDTO> findAccountsByNeighborhoods(EntityType entityType, 
			List<NeighborhoodDTO> neighborhoods, int page, int pageSize);
	
	
	/**
	 * Finds PersonalAccount based on neighboordhood and Gender
	 * @param gender
	 * @param neighborhoodId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	List<PersonalAccountDTO> findPersonalAccounts(Gender gender, 
			long neighborhoodId, 
			int start,
			int pageSize);
	
	
	/**
	 * Finds total PersonalAccountCount by gender
	 * @param entityType
	 * @param gender
	 * @param neighborhoodId
	 * @return
	 */
	Long getTotalPersonalAccountCountByGender(
			Gender gender, 
			Long neighborhoodId);
}
