package com.ziplly.app.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.common.collect.Maps;

public class EntitManagerService {
	static Map<String, String> properties = Maps.newHashMap();
//	static {
//		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
//			properties.put("javax.persistence.jdbc.driver",
//					"com.mysql.jdbc.GoogleDriver");
//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("jdbc:google:mysql://zipplyrocks:zipllydb/zipllydb"));
//		} else {
//			properties.put("javax.persistence.jdbc.driver",
//					"com.mysql.jdbc.Driver");
//			properties.put("javax.persistence.jdbc.url",
//					System.getProperty("cloudsql.url.dev"));
//		}
//	}
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb");
	
	private static EntitManagerService INSTANCE = new EntitManagerService();
	private EntitManagerService() {
	}
	
	public static EntitManagerService getInstance() {
		return INSTANCE;
	}
	
	public EntityManager getEntityManager() {
		return INSTANCE.emf.createEntityManager();
	}
}
