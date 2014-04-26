package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;

public class TweetDetailsPlace extends Place {
	private Long tweetId;
	private String couponRedeemCode;
	
	public TweetDetailsPlace() {
  }
	
	@Prefix("tweet")
	public static class Tokenizer extends BaseTokenizer<TweetDetailsPlace> {
		@Override
		public TweetDetailsPlace getPlace(String token) {
			try {
				tokenize(token);
				TweetDetailsPlace place = new TweetDetailsPlace();
				String token0 = getTokenAt(0);
				
				if (StringConstants.COUPON_REDEEM_TOKEN.equalsIgnoreCase(token0)) {
					String token1 = getTokenAt(1);
					place.setCouponRedeemCode(token1);
					return place;
				}

				long tweetId = Long.parseLong(token);
				place.setTweetId(tweetId);
				return place;
			} catch (Exception ex) {
				return new TweetDetailsPlace(0L);
			}
		}

		@Override
		public String getToken(TweetDetailsPlace place) {
			if (place.getTweetId() != null) {
				return place.getTweetId().toString();
			}
			return "";
		}
	}

	public TweetDetailsPlace(Long tweetId) {
		this.tweetId = tweetId;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public String getCouponRedeemCode() {
	  return couponRedeemCode;
  }

	public void setCouponRedeemCode(String couponRedeemCode) {
	  this.couponRedeemCode = couponRedeemCode;
  }
}
