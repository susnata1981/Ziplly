package com.ziplly.app.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.AccountNotificationDAOImpl;
import com.ziplly.app.dao.AccountRegistrationDAO;
import com.ziplly.app.dao.AccountRegistrationDAOImpl;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.CommentDAOImpl;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.ConversationDAOImpl;
import com.ziplly.app.dao.HashtagDAO;
import com.ziplly.app.dao.HashtagDAOImpl;
import com.ziplly.app.dao.ImageDAO;
import com.ziplly.app.dao.ImageDAOImpl;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.InterestDAOImpl;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.LikeDAOImpl;
import com.ziplly.app.dao.LocationDAO;
import com.ziplly.app.dao.LocationDAOImpl;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.NeighborhoodDAOImpl;
import com.ziplly.app.dao.PasswordRecoveryDAO;
import com.ziplly.app.dao.PasswordRecoveryDAOImpl;
import com.ziplly.app.dao.PendingInvitationsDAO;
import com.ziplly.app.dao.PendingInvitationsDAOImpl;
import com.ziplly.app.dao.PostalCodeDAO;
import com.ziplly.app.dao.PostalCodeDAOImpl;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SessionDAOImpl;
import com.ziplly.app.dao.SpamDAO;
import com.ziplly.app.dao.SpamDAOImpl;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.SubscriptionPlanDAOImpl;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.dao.TransactionDAOImpl;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.dao.TweetDAOImpl;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AccountBLIImpl;
import com.ziplly.app.server.AdminBLI;
import com.ziplly.app.server.AdminBLIImpl;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.server.EmailServiceImpl;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.server.PaymentServiceImpl;
import com.ziplly.app.server.TweetNotificationBLI;
import com.ziplly.app.server.TweetNotificationBLIImpl;

public class DAOModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountDAO.class).to(AccountDAOImpl.class).in(Singleton.class);
		bind(TweetDAO.class).to(TweetDAOImpl.class).in(Singleton.class);
		bind(SessionDAO.class).to(SessionDAOImpl.class).in(Singleton.class);
		bind(InterestDAO.class).to(InterestDAOImpl.class).in(Singleton.class);
		bind(CommentDAO.class).to(CommentDAOImpl.class).in(Singleton.class);
		bind(LikeDAO.class).to(LikeDAOImpl.class).in(Singleton.class);
		bind(TweetNotificationBLI.class).to(TweetNotificationBLIImpl.class).in(Singleton.class);
		bind(AccountBLI.class).to(AccountBLIImpl.class).in(Singleton.class);
		bind(AdminBLI.class).to(AdminBLIImpl.class).in(Singleton.class);
		bind(PaymentService.class).to(PaymentServiceImpl.class).in(Singleton.class);
		bind(EmailService.class).to(EmailServiceImpl.class).in(Singleton.class);
		bind(ConversationDAO.class).to(ConversationDAOImpl.class).in(Singleton.class);
		bind(TransactionDAO.class).to(TransactionDAOImpl.class).in(Singleton.class);
		bind(SubscriptionPlanDAO.class).to(SubscriptionPlanDAOImpl.class).in(Singleton.class);
		bind(PasswordRecoveryDAO.class).to(PasswordRecoveryDAOImpl.class).in(Singleton.class);
		bind(AccountRegistrationDAO.class).to(AccountRegistrationDAOImpl.class).in(Singleton.class);
		bind(AccountNotificationDAO.class).to(AccountNotificationDAOImpl.class).in(Singleton.class);
		bind(HashtagDAO.class).to(HashtagDAOImpl.class).in(Singleton.class);
		bind(SpamDAO.class).to(SpamDAOImpl.class).in(Singleton.class);
		bind(NeighborhoodDAO.class).to(NeighborhoodDAOImpl.class).in(Singleton.class);
		bind(PostalCodeDAO.class).to(PostalCodeDAOImpl.class).in(Singleton.class);
		bind(PendingInvitationsDAO.class).to(PendingInvitationsDAOImpl.class).in(Singleton.class);
		bind(LocationDAO.class).to(LocationDAOImpl.class).in(Singleton.class);
		bind(ImageDAO.class).to(ImageDAOImpl.class).in(Singleton.class);
	}

//	@Provides
//	public EntityManager getEntityManager() {
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb");
//		return emf.createEntityManager();
//	}
}
