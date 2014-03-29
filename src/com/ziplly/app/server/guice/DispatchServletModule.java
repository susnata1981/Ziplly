package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.inject.servlet.ServletModule;
import com.ziplly.app.server.NotificationServlet;
import com.ziplly.app.server.UploadServlet;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		serve("/ziplly/sendmail", "/_ah/start").with(NotificationServlet.class);
		serve("/ziplly/upload").with(UploadServlet.class);
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);

		// TODO needs to be turned on later.
		// serve("/ziplly/cofirmpayment").with(ConfirmPaymentServlet.class);
	}
}
