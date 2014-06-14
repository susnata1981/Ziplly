package com.ziplly.app.client.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ziplly.app.model.overlay.SubscriptionDTO;

public class CommonUtil {
  
  public static void sort(List<SubscriptionDTO> subscriptions) {
    Collections.sort(subscriptions, new Comparator<SubscriptionDTO>() {

      @Override
      public int compare(SubscriptionDTO o1, SubscriptionDTO o2) {
        return o2.getTimeCreated().compareTo(o1.getTimeCreated());
      }
      
    });
  }
}
