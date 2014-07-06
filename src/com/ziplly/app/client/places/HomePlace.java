package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.model.TweetType;

public class HomePlace extends Place {
	private long tweetId;
	private String tweetType;
	private String couponCode;

	public HomePlace() {
		this(TweetType.ALL);
	}

	public HomePlace(TweetType type) {
		this.tweetType = type.name().toLowerCase();
	}

	public HomePlace(Long tweetId) {
	  this();
		this.tweetId = tweetId;
	}

	public String getTweetType() {
		return tweetType.toLowerCase();
	}

	public void setTweetType(TweetType tweetType) {
		this.tweetType = tweetType.name();
	}
	
	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}

	@Prefix("home")
	public static class Tokenizer extends BaseTokenizer<HomePlace> {

	  @Override
    public HomePlace getPlace(String token) {
      try {
        HomePlace place = new HomePlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          String value = params.get(key).value();
          if (key.equals(AttributeKey.TWEET_ID)) {
            place.setTweetId(Long.parseLong(value));
          } else if (key.equals(AttributeKey.TWEET_CATEGORY)) {
            place.setTweetType(TweetType.valueOf(value.toUpperCase()));
          }
        }
        
        return place;
      } catch (Exception ex) {
        return new HomePlace();
      }
    }
	  
		@Override
		public String getToken(HomePlace place) {
		  return PlaceUtils.getPlaceToken(place);
		}
	}

	public void setCouponCode(String couponRedeemCode) {
		this.couponCode = couponRedeemCode;
  }
	
	public String getCouponCode() {
		return couponCode;
	}
}
