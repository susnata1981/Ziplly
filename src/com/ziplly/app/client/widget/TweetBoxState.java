package com.ziplly.app.client.widget;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.ImageDTO;

public class TweetBoxState {
	private State state;
	private ImageState imageState;
	private ImageDTO currentUploadedImage;
	private CouponDTO currentCoupon;

	public enum State {
		PREVIEW_ENABLED, PREVIEW_DISABLED;
	}

	public enum ImageState {
		PRESENT, ABSENT;
	}

	public TweetBoxState() {
		state = State.PREVIEW_DISABLED;
		imageState = ImageState.ABSENT;
	}

	public void displayImage() {
		state = State.PREVIEW_ENABLED;
	}

	public void previewImage(ImageDTO currentUploadedImage) {
		this.currentUploadedImage = currentUploadedImage;
		state = State.PREVIEW_ENABLED;
		imageState = ImageState.PRESENT;
	}

	public void cancel() {
		state = State.PREVIEW_DISABLED;
		imageState = ImageState.ABSENT;
	}

	public void reset() {
		cancel();
	}

	public void toggle() {
		state = (state == State.PREVIEW_DISABLED) ? State.PREVIEW_ENABLED : State.PREVIEW_DISABLED;
	}

	public boolean isImageUploaded() {
		return imageState == ImageState.PRESENT;
	}

	public boolean isPreviewPanelVisible() {
		return state == State.PREVIEW_ENABLED;
	}

	public void setPreview(boolean enable) {
		state = enable ? State.PREVIEW_ENABLED : State.PREVIEW_DISABLED;
	}
	
	public ImageDTO getCurrentUploadedImage() {
		return currentUploadedImage;
	}

	public void setCoupon(CouponDTO coupon) {
		this.setCurrentCoupon(coupon);
  }

	public CouponDTO getCurrentCoupon() {
	  return currentCoupon;
  }

	public void setCurrentCoupon(CouponDTO currentCoupon) {
	  this.currentCoupon = currentCoupon;
  }

	public void clear() {
		this.currentCoupon = null;
		this.imageState = ImageState.ABSENT;
		this.currentUploadedImage = null;
		this.state = State.PREVIEW_DISABLED;
  }

	public boolean hasCoupon() {
		return currentCoupon != null;
  }
}
