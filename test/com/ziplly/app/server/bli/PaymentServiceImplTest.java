package com.ziplly.app.server.bli;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.repackaged.org.joda.time.Days;
import com.ziplly.app.base.AbstractBase;

public class PaymentServiceImplTest extends AbstractBase {
  private PaymentService payementService;

  @Before
  public void setUp() {
    this.payementService = getInstance(PaymentService.class);
  }
  
  @Test
  public void getSubscriptionStartTimeTest() {
    PaymentServiceImpl impl = (PaymentServiceImpl)payementService;
    DateTime now = new DateTime();
    long subscriptionStartMillis = impl.getSubscriptionStartTime(now);
    DateTime subscriptionStart = new DateTime(subscriptionStartMillis);
    DateTime minusDays = subscriptionStart.minusDays(30);
    assertEquals(now, minusDays);
  }
}
