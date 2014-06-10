package com.ziplly.app.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.server.model.jpa.Account;

public class BaseDAO {
	protected final Provider<EntityManager> entityManagerProvider;
	private Set<Long> blackListedAccounts;

	private final Predicate<Account> BLACKLISTED_ACCOUNT_FILTER = new Predicate<Account>() {

    @Override
    public boolean apply(Account account) {
      return !getBlackListedAccounts().contains(account.getAccountId());
    }
	};
	
	@Inject
	public BaseDAO(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
		blackListedAccounts = new HashSet<Long>();
		loadSellerBlackList();
  }
	
	private void loadSellerBlackList() {
	  String property = System.getProperty("app.blackListAccounts");
	  String[] sellerIdStringList = property.split(",");
	  for(String sid : sellerIdStringList) {
	    blackListedAccounts.add(Long.parseLong(sid));
	  }
  }

  public final EntityManager getEntityManager() {
		return entityManagerProvider.get();
	}
  
  public <E extends Account> List<E> filterBlacklistedAccounts(Iterable<E> accounts) {
    Iterable<E> result = Iterables.filter(accounts, BLACKLISTED_ACCOUNT_FILTER);
    return Lists.newArrayList(result);
  }

  public Set<Long> getBlackListedAccounts() {
    return ImmutableSet.copyOf(blackListedAccounts);
  }
}
