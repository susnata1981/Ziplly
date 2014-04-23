package com.ziplly.app.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.server.bli.ServiceModule;
import com.ziplly.app.server.handlers.ZipllyActionHandlerModule;

public class MyGuiceContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
		    new DispatchServletModule(),
		    new ZipllyActionHandlerModule(),
		    new DAOModule(),
		    new ServiceModule());
	}
}
