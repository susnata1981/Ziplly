package com.ziplly.app.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;

public class EntityManagerService {
	public static Map<String, String> properties = Maps.newHashMap();

	static {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
			properties.put(
			    "javax.persistence.jdbc.url",
			    "jdbc:google:mysql://zipplyrocks:z1/zipllydbnew?user=zipllyadmin");
			properties.put("javax.persistence.jdbc.user", "zipllyadmin");
			properties.put("javax.persistence.jdbc.password", "Sherica12");
		} 
		else {
			properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
			properties.put("javax.persistence.jdbc.url", "jdbc:mysql://127.0.0.1:3306/zipllydbnew");
			properties.put("javax.persistence.jdbc.user", "zipllyadmin");
			properties.put("javax.persistence.jdbc.password", "Sherica12");
		}
	}

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("zipllydb", properties);
	private static EntityManagerService INSTANCE = new EntityManagerService();

	public EntityManagerService() {
	}

	public static EntityManagerService getInstance() {
		return INSTANCE;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	// private void testDbConnection() {
	// try {
	// Connection conn = (Connection)
	// DriverManager.getConnection("jdbc:google:mysql://zipplyrocks:zip/zipllydb?user=root");
	// try {
	// Statement st = conn.createStatement();
	// ResultSet rs = st.executeQuery("SHOW DATABASES");
	// while (rs.next()) {
	// System.out.println(rs.getString(1));
	// }
	// System.out.println("-- done --");
	// } finally {
	// conn.close();
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
}
