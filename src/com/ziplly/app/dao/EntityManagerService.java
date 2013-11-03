package com.ziplly.app.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.common.collect.Maps;

public class EntityManagerService {
	static Map<String, String> properties = Maps.newHashMap();
//	static {
//		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
//			properties.put("javax.persistence.jdbc.driver",
//					"com.google.appengine.api.rdbms.AppEngineDriver");
//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("jdbc:google:mysql://zipplyrocks:zipllydb1/zipllydb"));
//		} else {
//			properties.put("javax.persistence.jdbc.driver",
//					"com.google.appengine.api.rdbms.AppEngineDriver");
//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("cloudsql.url.dev"));
//		}
//	}
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb");
	
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
