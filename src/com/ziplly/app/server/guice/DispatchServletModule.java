package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.inject.servlet.ServletModule;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);
		// serve("/ziplly/sendmail").with(NotificationServlet.class);
		// serve("/upload/*").with(UploadServlet.class);
		// serve("/sendmail").with(EmailServlet.class);
		// serve("/cofirmpayment").with(ConfirmPaymentServlet.class);
		// serve("/ziplly/zipllyservice").with(ZipllyServiceImpl.class);
	}
}
