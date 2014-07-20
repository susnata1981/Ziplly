package com.ziplly.app.client.places;

public enum AttributeKey {
  TRANSACTION_VIEW_TOKEN("showTransaction"),
  ACCOUNT_ID("accountId"),
  TWEET_ID("tweetId"),
  TWEET_CATEGORY("tweetCategory"),
  CONVERSATION_ID("conversationId"),
  COUPON_REDEEM("couponRedeem"),
  SEND_MESSAGE_TOKEN("sendMessage"),
  NEIGHBORHOOD_ID("neighborhoodId"),
  POSTAL_CODE("postalCode"),
  GENDER_KEY("gender"),
  SEND_MESSAGE("sendMessage"),
  NONE("none");
  
  private String name;

  AttributeKey(String name) {
    this.setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static AttributeKey findByName(String t) {
    for(AttributeKey key : values()) {
      if (key.getName().equalsIgnoreCase(t)) {
        return key;
      }
    }
    
    return NONE;
  }
}
