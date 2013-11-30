package com.ziplly.app.client.activities;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

public class TweetViewBinder {
	private static final int THRESHOLD = 300;
	private static final int REFRESH_RATE = 200;

	Timer timer;
	Element elem;
	InfiniteScrollHandler handler;

	public TweetViewBinder(Element elem, InfiniteScrollHandler handler) {
		this.elem = elem;
		this.handler = handler;
	}
	
	public void start() {
		timer = new Timer() {
			@Override
			public void run() {
				if (hasMoreData()) {
					if (detectScrollerHitBottom()) {
						handler.onScrollBottomHit();
					}
				} else {
					cancel();
				}
			}
		};
		timer.scheduleRepeating(REFRESH_RATE);
	}

	public void stop() {
		timer.cancel();
		System.out.println("Stopping TweetViewBinder.");
	}

	private boolean detectScrollerHitBottom() {
		return elem.getScrollHeight() - (elem.getScrollTop() + elem.getOffsetHeight()) < THRESHOLD;
	}

	private boolean hasMoreData() {
		return handler.hasMoreElements();
	}
}