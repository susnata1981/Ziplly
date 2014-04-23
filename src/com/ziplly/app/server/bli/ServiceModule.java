package com.ziplly.app.server.bli;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.ziplly.app.server.crypto.CryptoModule;

/**
 * Factory for BLI's
 */
public class ServiceModule extends AbstractModule {

	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
	public @interface CouponRedeemEndpoint {
	}
	
	@Override
  protected void configure() {
		Properties serviceProperties;
    try {
	    serviceProperties = loadProperties("service.properties");
			Names.bindProperties(binder(), serviceProperties);
    } catch (IOException e) {
    	throw new RuntimeException("Failed to read service.properties");
    }

    install(new CryptoModule());
    
		bind(TweetBLI.class).to(TweetBLIImpl.class).in(Singleton.class);
		bind(AccountBLI.class).to(AccountBLIImpl.class).in(Singleton.class);
		bind(AdminBLI.class).to(AdminBLIImpl.class).in(Singleton.class);
		bind(PaymentService.class).to(PaymentServiceImpl.class).in(Singleton.class);
		bind(EmailService.class).to(EmailServiceImpl.class).in(Singleton.class);
		bind(CouponBLI.class).to(CouponBLIImpl.class).in(Singleton.class);
  }
	
	private static Properties loadProperties(String name) throws IOException {
		Properties properties = new Properties();
		InputStream is = new Object() {}.getClass().getEnclosingClass().getResourceAsStream(name);
		try {
			properties.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		return properties;
	}
	
	@Provides
	@CouponRedeemEndpoint
	public String getCouponRedeemEndpoint() {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return "http://127.0.0.1:8888/ziplly/redeem?code=";
		} else {
			return "http://www.ziplly.com/ziplly/redeem?code=";
		}
	}
	
}