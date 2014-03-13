package com.ziplly.app.client.activities;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

public class TweetViewBinder {
	private static final int REFRESH_RATE = 200;
	private static final int THRESHOLD = 300;

	Element elem;
	InfiniteScrollHandler handler;
	Timer timer;

	public TweetViewBinder(final Element elem, final InfiniteScrollHandler handler) {
		this.elem = elem;
		this.handler = handler;
	}

	protected boolean detectScrollerHitBottom() {
		//		int sh = elem.getScrollHeight();
		//		int st = elem.getScrollTop();
		//		int of = elem.getOffsetHeight();
		// System.out.println("SH="+sh+" ST="+st+" OF="+of);
		// if (scrollTop == st) {
		// return false;
		// }
		//		scrollTop = st;
		return elem.getScrollHeight() - (elem.getScrollTop() + elem.getOffsetHeight()) < THRESHOLD;
	}

	private boolean hasMoreData() {
		return handler.hasMoreElements();
	}

	//	int scrollTop = 0;

	public void start() {
		// System.out.println("Starting tweet view binder...");
		timer = new Timer() {
			@Override
			public void run() {
				// System.out.println("RUNNING TWEETVIEWBINDER...");
				if (hasMoreData()) {
					if (detectScrollerHitBottom()) {
						handler.onScrollBottomHit();
					}
				} else {
					// System.out.println("Cancelling TweetViewBinder timer");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(REFRESH_RATE);
	}

	public void stop() {
		timer.cancel();
		// System.out.println("Stopping TweetViewBinder.");
	}
}
