package com.ziplly.app.server.bli;

import java.util.Date;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.base.AbstractBase;

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
		Date now = new Date();
		DateTime subscriptionStart = new DateTime(now).minusDays(5);
		Date lastSubscriptionCycle = bli.getLastSubscriptionCycle(subscriptionStart.toDate());
		DateTime result = new DateTime(lastSubscriptionCycle);
		assertEquals("Day doesn't match", subscriptionStart.getDayOfMonth(), result.getDayOfMonth());
		assertEquals("Month doesn't match", subscriptionStart.getMonthOfYear() - 1, result.getMonthOfYear());
		assertEquals("Year doesn't match", subscriptionStart.getYear(), result.getYear());
	}
	
	@Test
	public void getLastSubscriptionCycleAfterTest() {
		TweetBLIImpl bli = (TweetBLIImpl)tweetBli;
		DateTime currTime = new DateTime();

		// 1st Jan
		DateTime subscriptionStart = new DateTime(2013, 1, currTime.getDayOfMonth()+5, 1, 1);
		Date lastSubscriptionCycle = bli.getLastSubscriptionCycle(subscriptionStart.toDate());
		DateTime result = new DateTime(lastSubscriptionCycle);
		assertEquals("Day doesn't match", subscriptionStart.getDayOfMonth(), result.getDayOfMonth());
		assertEquals("Month doesn't match", currTime.getMonthOfYear() - 1, result.getMonthOfYear());
		assertEquals("Year doesn't match", currTime.getYear(), result.getYear());
	}
	
	@Test
	public void testMonthOfYear() {
		DateTime time = new DateTime();
		assertEquals("Month doesn't match", 5, time.getMonthOfYear());
	}
}
