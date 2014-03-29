package com.ziplly.app.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class BaseDAO {
	protected final Provider<EntityManager> entityManagerProvider;
	
	@Inject
	public BaseDAO(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
  }
	
	public final EntityManager getEntityManager() {
		return entityManagerProvider.get();
	}
}
