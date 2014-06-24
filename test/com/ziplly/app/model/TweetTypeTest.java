package com.ziplly.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TweetTypeTest {
  
  @Test
  public void tweetTypesRequiringEmailTest() {
    assertEquals(
        ImmutableList.of(
            TweetType.ANNOUNCEMENT,
            TweetType.SECURITY_ALERTS,
            TweetType.OFFERS,
//            TweetType.HOT_DEALS,
            TweetType.COUPON),
        TweetType.getTweetTypesForRequiringNotification());
  }
  
}
