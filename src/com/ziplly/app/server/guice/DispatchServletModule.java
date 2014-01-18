package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.inject.servlet.ServletModule;
import com.ziplly.app.server.ConfirmPaymentServlet;
import com.ziplly.app.server.EmailServlet;

public class DispatchServletModule extends ServletModule {
	
	@Override
	public void configureServlets() {
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);
		serve("/sendmail").with(EmailServlet.class);
		serve("/cofirmpayment").with(ConfirmPaymentServlet.class);
//		serve("/upload/*").with(UploadServlet.class);
//		serve("/ziplly/zipllyservice").with(ZipllyServiceImpl.class);
	}
}
