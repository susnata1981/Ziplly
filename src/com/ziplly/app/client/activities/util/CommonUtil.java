package com.ziplly.app.client.activities.util;

import com.google.gwt.user.client.Window;

public class CommonUtil {
  
  public static String getUrlParameter(String key) {
    return Window.Location.getParameter(key);
  }
}
