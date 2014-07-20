package com.ziplly.app.client.activities.util;

import java.util.Map;

import com.google.gwt.user.client.Window;
import com.ziplly.app.client.exceptions.InvalidUrlException;
import com.ziplly.app.client.places.AttributeKey;
import com.ziplly.app.client.places.AttributeValue;
import com.ziplly.app.client.places.PlaceParser;
import com.ziplly.app.client.places.PlaceParserImpl;

public class CommonUtil {

  public static String getUrlParameter(String key) {
    return Window.Location.getParameter(key);
  }

  public static String getPlaceParam(String token, AttributeKey key) throws InvalidUrlException {
    PlaceParser parser = new PlaceParserImpl();
    String hash = Window.Location.getHash();
    if (hash.length() > token.length() + 1) {
      // 1 + 1 = '#' + ':'
      String params = hash.substring(token.length() + 2);
      Map<AttributeKey, AttributeValue> valueMap = parser.parse(params);
      return valueMap.get(key).value();
    }

    throw new InvalidUrlException(Window.Location.getHref());
  }
}
