package com.ziplly.app.shared.facebook;

import com.restfb.Facebook;

public class FStatus {
	@Facebook("time")
	public long time;

	@Facebook("like_info")
	public LikeInfo likeInfo;

	public static class LikeInfo {
		@Facebook("like_count")
		public long likeCount;
	}
}
