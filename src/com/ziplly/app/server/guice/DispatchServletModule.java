package com.ziplly.app.server.guice;

import java.util.Map;
import java.util.Properties;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.ziplly.app.server.ConfirmPaymentServlet;
import com.ziplly.app.server.EchoServlet;
import com.ziplly.app.server.NotificationServlet;
import com.ziplly.app.server.UploadServlet;

public class DispatchServletModule extends ServletModule {
  public static Map<String, String> dbProperties = Maps.newHashMap();
  public static Properties dbConfig = new Properties();

  static {
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
      dbProperties.put(
          "javax.persistence.jdbc.url",
          "jdbc:google:mysql://zipplyrocks:z1/zipllydbnew?user=zipllyadmin");
      dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
      dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
    }
//    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
//      dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.GoogleDriver");
//      dbProperties.put(
//          "javax.persistence.jdbc.url",
//          "jdbc:google:mysql://zipllydevel:z1/zipllydb?user=zipllyadmin");
//      dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
//      dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
//    }
    else {
      dbProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
      dbProperties.put("javax.persistence.jdbc.url", "jdbc:mysql://127.0.0.1:3306/zipllydbnew");
      dbProperties.put("javax.persistence.jdbc.user", "zipllyadmin");
      dbProperties.put("javax.persistence.jdbc.password", "Sherica12");
    }
    dbConfig.putAll(dbProperties);
  }
  
	@Override
	public void configureServlets() {
	  install(new JpaPersistModule("zipllydb").properties(dbConfig));
		filter("/*").through(PersistFilter.class);
		
//		bind(UploadServlet.class).in(Singleton.class);
//		serve("/ziplly/upload").with(UploadServlet.class);
		serve("/ziplly/echo").with(EchoServlet.class);
		
		serve("/ziplly/sendmail", "/_ah/start").with(NotificationServlet.class);
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);
		serve("/ziplly/confirmpayment").with(ConfirmPaymentServlet.class);
		
		// TODO needs to be turned on later.
	}
}
