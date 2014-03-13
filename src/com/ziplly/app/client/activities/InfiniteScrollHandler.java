package com.ziplly.app.client.activities;

public interface InfiniteScrollHandler {
	boolean hasMoreElements();

	void onScrollBottomHit();
}
