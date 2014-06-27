package com.ziplly.app.dao;

import java.util.Collection;
import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.model.jpa.Account;

public interface AccountDAO {
  Account findByEmail(String email) throws NotFoundException;

  AccountDTO findByEmailAndPassword(String email, String password) throws NotFoundException;

  Account findById(Long accountId) throws NotFoundException;

  Account save(Account user);

  void updatePassword(Account account);

  Account update(Account user);

  List<Account> getAll(int start, int end);

  List<PersonalAccountDTO> findByZip(int zip);

  List<AccountDTO> findAll();

  List<AccountDTO> findAccounts(String query, int start, int end);

  Long findTotalAccounts(String countQuery);

  @Deprecated
  List<AccountDTO> findAllAccountsByZip(int zip);

  List<AccountDTO> findAccountsByNeighborhood(EntityType entityType,
      Long neighborhoodId,
      int pageStart,
      int pageSize);

  Long
      findTotalAccountsByNeighborhood(EntityType type, Long neighborhoodId) throws NotFoundException;

  Long getTotalAccountCountByNeighborhoods(EntityType entityType, List<Long> neighborhoodIds);

  Collection<? extends AccountDTO> findAccountsByNeighborhoods(EntityType entityType,
      List<Long> neighborhoodIds,
      int page,
      int pageSize);

  /**
   * Finds PersonalAccount based on neighboordhood and Gender
   * 
   * @param gender
   * @param neighborhoodId
   * @param start
   * @param pageSize
   * @return
   * @throws NotFoundException
   */
  List<PersonalAccountDTO> findPersonalAccounts(Gender gender,
      long neighborhoodId,
      int start,
      int pageSize) throws NotFoundException;

  /**
   * Finds total PersonalAccountCount by gender
   * 
   * @param entityType
   * @param gender
   * @param neighborhoodId
   * @return
   */
  Long getTotalPersonalAccountCountByGender(Gender gender, Long neighborhoodId);

  List<BusinessAccountDTO>
      findBusinessAccounts(long neighborhoodId, int start, int pageSize) throws NotFoundException;

  void updateLocation(AccountDTO accountDTO, LocationDTO location) throws NotFoundException;

  /**
   * Gets the list of new accounts of the specified type based on the
   * {@link EntityType}.
   * 
   * @param daysLookback
   * @param neighborhoodId
   * @param entityType
   * @return
   */
  List<AccountDTO> getAccountCreatedWithin(int daysLookback,
      long neighborhoodId,
      EntityType entityType);
}
