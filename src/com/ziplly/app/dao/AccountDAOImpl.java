package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.ziplly.app.model.Account;

public class AccountDAOImpl implements AccountDAO {

	@Override
	public Account findByEmail(String email)
			throws NotFoundException {
		// TODO
		throw new NotFoundException(); 
	}

	@Override
	public Account findById(Long accountId)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Account account) {
		EntityManager em = EntitManagerService.getEntityManager();
		em.getTransaction().begin();
		em.persist(account);
		em.getTransaction().commit();
		em.close();
		
		em = EntitManagerService.getEntityManager();
		em.getTransaction().begin();
		List<Account> accounts = em.createQuery("FROM account", Account.class).getResultList();
		for(Account a: accounts) {
			System.out.println(a.getDisplayName());
		}
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public List<com.ziplly.app.model.Account> get(
			com.ziplly.app.model.QueryMetaData qmd)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean save(com.ziplly.app.model.AccountDetails ad) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<com.ziplly.app.model.Account> getAll(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
