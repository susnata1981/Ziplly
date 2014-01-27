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
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			System.out.println("Setting database properties...");
			properties.put("javax.persistence.jdbc.driver","com.mysql.jdbc.GoogleDriver");
			properties.put("javax.persistence.jdbc.url", "jdbc:google:mysql://zipplyrocks:zipllydb/zipllydb?user=root");
//			properties.put("hibernate.connection.url", "jdbc:google:mysql://zipplyrocks:z/zipllydb");

//			properties.put("javax.persistence.jdbc.user", "root");
//			properties.put("javax.persistence.jdbc.password", "");
		} else {
			properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
			properties.put("javax.persistence.jdbc.url", "jdbc:mysql://127.0.0.1:3306/zipllydb");
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
//		testDbConnection();
	}
	
	public static EntityManagerService getInstance() {
		return INSTANCE;
	}
	
	public EntityManager getEntityManager() {
		return INSTANCE.emf.createEntityManager();
	}
	
//	private void testDbConnection() {
//		try {
//		      Connection conn = (Connection) DriverManager.getConnection("jdbc:google:mysql://zipplyrocks:zip/zipllydb?user=root");
//		      try {
//		        Statement st = conn.createStatement();
//		        ResultSet rs = st.executeQuery("SHOW DATABASES");
//		        while (rs.next()) {
//		          System.out.println(rs.getString(1));
//		        }
//		        System.out.println("-- done --");
//		      } finally {
//		        conn.close();
//		      }
//		    } catch (SQLException e) {
//		      e.printStackTrace();
//		    }
//	}
}
