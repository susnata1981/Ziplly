package com.ziplly.app.client.resource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.DataResource.DoNotEmbed;
import com.google.gwt.resources.client.ImageResource;

public interface ZResources extends ClientBundle {
	ZResources IMPL = GWT.create(ZResources.class);

	interface Styles extends CssResource {
		String mainPageBackground();

		String fbBtnImage();

		String zipllyLogo();
		
		String selectedLink();
		
		String deselectLink();
		
		String promotionCycleImage();
		
		String googleBuyButtonImage();
	}

	@DoNotEmbed
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

	@DoNotEmbed
	@Source("no-photo.jpg")
	ImageResource noPhoto();

	@DoNotEmbed
	@Source("no_picture.gif")
	ImageResource noGeneralImage();
	
	@Source("magnolia.jpeg")
	ImageResource magnolia();

	@Source("bluecriscross.png")
	ImageResource profileBackground();

	@Source("uploadicon.png")
	ImageResource uploadIcon();
	
	@Source("promotionCycle5.png")
	@DoNotEmbed
	ImageResource promotionCycle();

	@Source("leftarrow.png")
	@DoNotEmbed
	ImageResource leftArrow();
	
	@Source("rightarrow.png")
  @DoNotEmbed
  ImageResource rightArrow();
	
	@Source("google-buy-button-resized.png")
	ImageResource googleBuyButton();
	
	@Source("zstyle.css")
	Styles style();
	
	/**
	 * @return
	 */
	@Source("media.css")
	TextResource mediaQueries();
	
	@Source("print.css")
	TextResource printCss();
	
	@Source("fineprint.txt")
	TextResource finePrint();
}
