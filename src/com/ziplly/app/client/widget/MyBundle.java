package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface MyBundle extends ClientBundle {
	
	@Source("Red-signin-Medium-base-32dp.png")
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource googleLoginButton();

	@Source("Large_230x36.png")
	ImageResource fbLoginBtn();
	
	@Source("FacebookLogo.jpg")
	ImageResource fbLogo();
	
	@Source("tom.jpeg")
	ImageResource tom();
	
	@Source("MyCss.css")
	public MyCssResource style(); 
	
	public interface MyCssResource extends CssResource {
		public String gButton();
		public String hide();
		public String heading();
		public String fbLoginBtn();
		public String fbLogo();
	}
	
	public static final MyBundle INSTANCE = GWT.create(MyBundle.class);
}
