package com.ziplly.app.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Properties;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.server.bli.TweetNotificationBLI;
import com.ziplly.app.server.bli.TweetNotificationBLIImpl;

public class DAOModule extends AbstractModule {
	private static final String LOCALHOST_APP_URL = "http://localhost:8888/Ziplly.html?gwt.codesvr=127.0.0.1%3A9997";
	public static Map<String, String> dbProperties = Maps.newHashMap();
	public static Properties dbConfig = new Properties();
	
	static {
//		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
//			dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
//			dbProperties.put(
//			    "javax.persistence.jdbc.url",
//			    "jdbc:google:mysql://zipplyrocks:z1/newzipllydb?user=zipllyadmin");
//			dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
//			dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
//		}
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			// DEVEL SETUP
			dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
			dbProperties.put(
			    "javax.persistence.jdbc.url",
			    "jdbc:google:mysql://zipllydevel:z1/newzipllydb?user=zipllyadmin");
			dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
			dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
		}
		else {
			dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
			dbProperties.put("javax.persistence.jdbc.url", "jdbc:mysql://127.0.0.1:3306/newzipllydb");
			dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
			dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
		}
		dbConfig.putAll(dbProperties);
	}
	
	@BindingAnnotation
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface BackendAddress {}
	
	@Override
	protected void configure() {
		bind(AccountDAO.class).to(AccountDAOImpl.class).in(Singleton.class);
		bind(TweetDAO.class).to(TweetDAOImpl.class).in(Singleton.class);
		bind(SessionDAO.class).to(SessionDAOImpl.class).in(Singleton.class);
		bind(InterestDAO.class).to(InterestDAOImpl.class).in(Singleton.class);
		bind(CommentDAO.class).to(CommentDAOImpl.class).in(Singleton.class);
		bind(LikeDAO.class).to(LikeDAOImpl.class).in(Singleton.class);
		bind(TweetNotificationBLI.class).to(TweetNotificationBLIImpl.class).in(Singleton.class);
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
		bind(CouponDAO.class).to(CouponDAOImpl.class).in(Singleton.class);
		bind(CouponTransactionDAO.class).to(CouponTransactionDAOImpl.class).in(Singleton.class);
		bind(EntityManagerService.class).in(Singleton.class);
		
		bind(String.class).annotatedWith(BackendAddress.class).toProvider(BackendUrlProvider.class);
		
		install(new JpaPersistModule("zipllydb").properties(dbConfig));
	}

	public static class BackendUrlProvider implements Provider<String> {

		@Override
    public String get() {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			  return BackendServiceFactory.getBackendService().getBackendAddress(
		        System.getProperty(ZipllyServerConstants.BACKEND_INSTANCE_NAME_1));
			} else {
				return LOCALHOST_APP_URL;
			}
    }
	}
}
