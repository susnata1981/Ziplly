package com.ziplly.app.server.bli;

import java.util.Date;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.base.AbstractBase;
import com.ziplly.app.server.util.TimeUtil;

@RunWith(JUnit4.class)
public class TweetBLIImplTest extends AbstractBase {
	private TweetBLI tweetBli;
	
	@Before
	public void setUp() {
		this.tweetBli = getInstance(TweetBLI.class);
	}
	
	@Test
	public void getLastSubscriptionCycleBeforeTest() {
		TweetBLIImpl bli = (TweetBLIImpl)tweetBli;
//		Date now = new Date();
		DateTime now = new DateTime(2014, 2, 4, 3, 00); 
		DateTime subscriptionStart = new DateTime(now).minusDays(5);
		Date subscriptionStartDate = subscriptionStart.toDate();
		DateTime result = bli.getLastSubscriptionCycle(now.toDate(), subscriptionStartDate);
		assertEquals("Day doesn't match", subscriptionStart.getDayOfMonth(), result.getDayOfMonth());
		assertEquals("Month doesn't match", subscriptionStart.getMonthOfYear(), result.getMonthOfYear());
		assertEquals("Year doesn't match", subscriptionStart.getYear(), result.getYear());
	}
	
	@Test
	public void getLastSubscriptionCycleAfterTest() {
		TweetBLIImpl bli = (TweetBLIImpl)tweetBli;
		DateTime currTime = new DateTime();

		// 1st Jan
		DateTime subscriptionStart = new DateTime(2014, 2, currTime.getDayOfMonth() - 5, 1, 1);
		DateTime lastSubscriptionCycle = bli.getLastSubscriptionCycle(currTime.toDate(), subscriptionStart.toDate());
		DateTime result = new DateTime(lastSubscriptionCycle);
		assertEquals("Day doesn't match", subscriptionStart.getDayOfMonth(), result.getDayOfMonth());
		assertEquals("Month doesn't match", currTime.getMonthOfYear() - 1, result.getMonthOfYear());
		assertEquals("Year doesn't match", currTime.getYear(), result.getYear());
	}
	
	@Test
  public void getLastSubscriptionCycleAfterTestCrossingYear() {
    TweetBLIImpl bli = (TweetBLIImpl)tweetBli;
    DateTime currTime = new DateTime(2014, 6, 6, 10, 0);

    // 1st Jan
    DateTime subscriptionStart = new DateTime(2013, 1, 1, 10, 0);
    DateTime result = bli.getLastSubscriptionCycle(currTime.toDate(), subscriptionStart.toDate());
    assertEquals("Day doesn't match", subscriptionStart.getDayOfMonth(), result.getDayOfMonth());
    assertEquals("Month doesn't match", currTime.getMonthOfYear() - 1, result.getMonthOfYear());
    assertEquals("Year doesn't match", currTime.getYear(), result.getYear());
  }
	
	@Test
	public void testMonthOfYear() {
		DateTime time = new DateTime();
		assertEquals("Month doesn't match", 5, time.getMonthOfYear());
	}
	
	@Test
	public void testDate() {
	  Date now = new Date();
    DateTime d = new DateTime(now);
    DateTime newDate = d.toDateTime(DateTimeZone.forID("America/Los_Angeles"));
    System.out.println("Before =" + d);
    System.out.println("After = "+newDate);
	}
}
