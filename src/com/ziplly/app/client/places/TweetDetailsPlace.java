package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class TweetDetailsPlace extends Place {
	private long tweetId;
	private String couponRedeemCode;
	
	public TweetDetailsPlace() {
  }
	
	@Prefix("tweet")
	public static class Tokenizer extends BaseTokenizer<TweetDetailsPlace> {

	  @Override
    public TweetDetailsPlace getPlace(String token) {
      try {
        TweetDetailsPlace place = new TweetDetailsPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          String value = params.get(key).value();
          if (key.equals(AttributeKey.COUPON_REDEEM)) {
            place.setCouponRedeemCode(value);
          } else if (key.equals(AttributeKey.TWEET_ID)) {
            place.setTweetId(Long.parseLong(value));
          }
        }
        
        return place;
      } catch (Exception ex) {
        return new TweetDetailsPlace();
      }
    }
	  
		@Override
		public String getToken(TweetDetailsPlace place) {
		  return PlaceUtils.getPlaceToken(place);
		}
	}

	public TweetDetailsPlace(Long tweetId) {
		this.tweetId = tweetId;
	}

	public long getTweetId() {
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
