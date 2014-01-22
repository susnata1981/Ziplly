package com.ziplly.app.client.widget;

public class TweetBoxState {
	private State state;
	private ImageState imageState;
	
	public enum State {
		PREVIEW_ENABLED,
		PREVIEW_DISABLED;
	}
	
	public enum ImageState {
		PRESENT,
		ABSENT;
	}
	
	public TweetBoxState() {
		state = State.PREVIEW_DISABLED;
		imageState = ImageState.ABSENT;
	}
	
	public void displayImage() {
		state = State.PREVIEW_ENABLED;
	}

	public void preview() {
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
}
