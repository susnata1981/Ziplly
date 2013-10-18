package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import com.google.inject.servlet.ServletModule;
import com.ziplly.app.server.UploadServlet;
import com.ziplly.app.server.ZipllyServiceImpl;

public class DispatchServletModule extends ServletModule {
	
	@Override
	public void configureServlets() {
		serve("/ziplly/dispatch").with(GuiceStandardDispatchServlet.class);
		serve("/ziplly/zipllyservice").with(ZipllyServiceImpl.class);
//		serve("/ziplly/upload/*").with(UploadServlet.class);
	}
}
