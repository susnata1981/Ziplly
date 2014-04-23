package com.ziplly.app.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.server.bli.ServiceModule;
import com.ziplly.app.server.guice.DispatchServletModule;
import com.ziplly.app.server.handlers.ZipllyActionHandlerModule;

public class AbstractBase {
	private Injector injector;
	
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
