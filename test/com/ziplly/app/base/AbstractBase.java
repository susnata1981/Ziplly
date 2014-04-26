package com.ziplly.app.base;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.server.bli.ServiceModule;
import com.ziplly.app.server.guice.DispatchServletModule;
import com.ziplly.app.server.handlers.ZipllyActionHandlerModule;

public class AbstractBase {
	private Injector injector;
	
	public static Long [] buyerIds = { 2L, 12L, 123L, 1232L, 13345L};
	public static Long [] sellerIds = { 3L, 13L, 124L, 1231L, 13345L};
	public static Long [] couponIds = { 4L, 14L, 125L, 1237L, 13345L};
	public static Long [] transactionIds = { 5L, 15L, 126L, 1239L, 13345L};
	
	public AbstractBase() {
		injector = Guice.createInjector(
		    new DispatchServletModule(),
		    new ZipllyActionHandlerModule(),
		    new DAOModule(),
		    new ServiceModule());
  }
	
	public <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}
}
