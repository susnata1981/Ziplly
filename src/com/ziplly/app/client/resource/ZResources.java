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
		
		String zipllyLogo();
	}
	
	@Source("no-photo.jpg")
	ImageResource noImage();
	
	@Source("zbackground.jpg")
	ImageResource neighborhoodLargePic();
	
	@Source("active_600.png")
	ImageResource facebookLoginButtonImage();

	@Source("ziplly-logo.png")
	ImageResource zipllyLogo();
	
	@Source("ajax-loader-large.gif")
	ImageResource loadingImageLarge();
	
	@Source("ajax-loader-small.gif")
	ImageResource loadingImageSmall();
	
	@Source("no-photo.jpg")
	ImageResource noPhoto();
	
	@Source("magnolia.jpg")
	ImageResource magnolia();
	
	@Source("green-backgrounds.png")
	ImageResource profileBackground();
	
	@Source("zstyle.css")
	Styles style();
}
