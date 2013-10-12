package com.ziplly.app.server.guice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SessionDAOImpl;

public class DAOModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountDAO.class).to(AccountDAOImpl.class).in(Singleton.class);
		bind(SessionDAO.class).to(SessionDAOImpl.class).in(Singleton.class);
	}

//	@Provides
//	public EntityManager getEntityManager() {
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb");
//		return emf.createEntityManager();
//	}
}
