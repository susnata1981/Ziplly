package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import com.ziplly.app.server.ConfirmPaymentServlet;
import com.ziplly.app.server.NotificationServlet;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		filter("/*").through(PersistFilter.class);
		 
		serve("/ziplly/sendmail", "/_ah/start").with(NotificationServlet.class);
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);
		serve("/ziplly/cofirmpayment").with(ConfirmPaymentServlet.class);
		
		// serve("/ziplly/upload").with(UploadServlet.class);
		// TODO needs to be turned on later.
	}
}
