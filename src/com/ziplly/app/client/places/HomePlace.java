package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.TweetType;

public class HomePlace extends Place {
	private Long tweetId;
	private String tweetType;
	private String couponCode;

	public HomePlace() {
		this(TweetType.ALL);
	}

	public HomePlace(TweetType type) {
		this.tweetType = type.name().toLowerCase();
	}

	public HomePlace(Long tweetId) {
		this.tweetId = tweetId;
	}

	public String getTweetType() {
		return tweetType;
	}

	public void setTweetType(String tweetType) {
		this.tweetType = tweetType;
	}

	public void setTweetType(TweetType tweetType) {
		this.tweetType = tweetType.name();
	}
	
	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	@Prefix("home")
	public static class Tokenizer extends BaseTokenizer<HomePlace> {
	
		@Override
		public HomePlace getPlace(String token) {
			try {
				tokenize(token);
				String token0 = getTokenAt(0);
				HomePlace place = new HomePlace();
				// Tweet details
				if (token0.startsWith(StringConstants.HOME_TWEET_TOKEN)) {
					long tweetId = Long.parseLong(getTokenAt(1));
					place.setTweetId(tweetId);
					return place;
				} 
				// Coupon redeem
				else if (StringConstants.COUPON_REDEEM_TOKEN.equalsIgnoreCase(token0)) {
					String token1 = getTokenAt(1);
					place.setCouponCode(token1);
					return place;
				} 
				// Tweet category
				else {
					TweetType type = TweetType.valueOf(getTokenAt(1).toUpperCase());
					place.setTweetType(type);
					return new HomePlace(type);
				}
			} catch (Exception ex) {
				// return default HomePlace.
			}
			
			return new HomePlace();
		}

		@Override
		public String getToken(HomePlace place) {
			if (place.getTweetId() != null) {
				return PlaceUtils.getHomePlaceTokenForMessaging(place.getTweetId());
			} else if (place.getTweetType() != null) {
				return PlaceUtils.getHomePlaceTokenForTweetType(place.getTweetType());
			} else {
				return "";
			}
		}
	}

	public void setCouponCode(String couponRedeemCode) {
		this.couponCode = couponRedeemCode;
  }
	
	public String getCouponCode() {
		return couponCode;
	}
}
