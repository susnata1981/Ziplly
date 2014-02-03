package com.ziplly.app.client.resource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface ZResources extends ClientBundle {
	ZResources IMPL = GWT.create(ZResources.class);

	interface Styles extends CssResource {
		String mainPageBackground();
		
		String fbBtnImage();
	}
	
	@Source("No_person.jpg")
	ImageResource noImage();
	
	@Source("neighborhood_large.jpg")
	ImageResource neighborhoodLargePic();
	
	@Source("rsz_1rsz_active_6002x.png")
	ImageResource facebookLoginButtonImage();
	
	@Source("zstyle.css")
	Styles style();
}
