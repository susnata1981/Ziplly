package com.ziplly.app.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.CommentDAOImpl;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.InterestDAOImpl;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.LikeDAOImpl;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SessionDAOImpl;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.dao.TweetDAOImpl;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AccountBLIImpl;

public class DAOModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountDAO.class).to(AccountDAOImpl.class).in(Singleton.class);
		bind(TweetDAO.class).to(TweetDAOImpl.class).in(Singleton.class);
		bind(SessionDAO.class).to(SessionDAOImpl.class).in(Singleton.class);
		bind(AccountBLI.class).to(AccountBLIImpl.class).in(Singleton.class);
		bind(InterestDAO.class).to(InterestDAOImpl.class).in(Singleton.class);
		bind(CommentDAO.class).to(CommentDAOImpl.class).in(Singleton.class);
		bind(LikeDAO.class).to(LikeDAOImpl.class).in(Singleton.class);
	}

//	@Provides
//	public EntityManager getEntityManager() {
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb");
//		return emf.createEntityManager();
//	}
}
