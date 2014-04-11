package com.ziplly.app.server.guice;

import com.google.inject.AbstractModule;
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

/**
 * Factory for BLI's
 */
public class ServiceModule extends AbstractModule {

	@Override
  protected void configure() {
		bind(TweetBLI.class).to(TweetBLIImpl.class).in(Singleton.class);
		bind(AccountBLI.class).to(AccountBLIImpl.class).in(Singleton.class);
		bind(AdminBLI.class).to(AdminBLIImpl.class).in(Singleton.class);
		bind(PaymentService.class).to(PaymentServiceImpl.class).in(Singleton.class);
		bind(EmailService.class).to(EmailServiceImpl.class).in(Singleton.class);
		bind(CouponBLI.class).to(CouponBLIImpl.class).in(Singleton.class);
  }

}
