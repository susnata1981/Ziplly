package com.ziplly.app.server.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Singleton;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AccountBLIImpl;
import com.ziplly.app.server.AdminBLI;
import com.ziplly.app.server.AdminBLIImpl;
import com.ziplly.app.server.CouponBLI;
import com.ziplly.app.server.CouponBLIImpl;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.server.EmailServiceImpl;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.server.PaymentServiceImpl;
import com.ziplly.app.server.TweetBLI;
import com.ziplly.app.server.TweetBLIImpl;
import com.ziplly.app.server.crypto.CryptoModule;

/**
 * Factory for BLI's
 */
public class ServiceModule extends AbstractModule {

	/*
	 * Seller ID
	 */
	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value= {ElementType.FIELD, ElementType.PARAMETER})
	public @interface SellerIdentifier {
	}
	
	/*
	 * Seller secret
	 */
	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value= {ElementType.FIELD, ElementType.PARAMETER})
	public @interface SellerSecret {
	}
	
	/*
	 * Seller secret
	 */
	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value= {ElementType.FIELD, ElementType.PARAMETER})
	public @interface QrcodeEndpoint {
	}
	
	@Override
  protected void configure() {
		bindConstant().annotatedWith(SellerIdentifier.class).to("01544868397547783964");
		bindConstant().annotatedWith(SellerSecret.class).to("xnKebOqL59b1P45EF1PUmg");
		bindConstant().annotatedWith(QrcodeEndpoint.class).to("https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=");
		
		bind(TweetBLI.class).to(TweetBLIImpl.class).in(Singleton.class);
		bind(AccountBLI.class).to(AccountBLIImpl.class).in(Singleton.class);
		bind(AdminBLI.class).to(AdminBLIImpl.class).in(Singleton.class);
		bind(PaymentService.class).to(PaymentServiceImpl.class).in(Singleton.class);
		bind(EmailService.class).to(EmailServiceImpl.class).in(Singleton.class);
		
		install(new CryptoModule());
		bind(CouponBLI.class).to(CouponBLIImpl.class).in(Singleton.class);
  }
}
