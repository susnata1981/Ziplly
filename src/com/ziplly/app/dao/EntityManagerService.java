package com.ziplly.app.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;

public class EntityManagerService {
	static Map<String, String> properties = Maps.newHashMap();
	static {
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
			System.out.println("Setting database properties...");
			properties.put("javax.persistence.jdbc.driver",
					"com.google.appengine.api.rdbms.AppEngineDriver");
//			properties.put("javax.persistence.jdbc.driver",
//					"com.google.appengine.api.rdbms.AppEngineDriver");
			properties.put("javax.persistence.jdbc.url", "jdbc:google:rdbms://zipplyrocks:zipplydb1/zipllydb");
		} else {
			properties.put("javax.persistence.jdbc.driver", "com.google.appengine.api.rdbms.AppEngineDriver");
			properties.put("javax.persistence.jdbc.url", "jdbc:google:mysql://zipplyrocks:zipllydb/zipllydb");
			properties.put("javax.persistence.jdbc.user", "root");

//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("cloudsql.url.dev"));
//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("jdbc:google:mysql://zipplyrocks:zipllydb1/zipllydb"));
		}
	}
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb", properties);
	
	private static EntityManagerService INSTANCE = new EntityManagerService();
	private EntityManagerService() {
	}
	
	public static EntityManagerService getInstance() {
		return INSTANCE;
	}
	
	public EntityManager getEntityManager() {
//		return INSTANCE.getEntityManagerFactory().createEntityManager();
		return INSTANCE.emf.createEntityManager();
	}
	
//	EntityManagerFactory getEntityManagerFactory() {
//		return Persistence.createEntityManagerFactory("zipllydb");
//	}
}
