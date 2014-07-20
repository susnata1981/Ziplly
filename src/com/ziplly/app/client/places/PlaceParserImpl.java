package com.ziplly.app.client.places;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.ziplly.app.client.exceptions.InvalidUrlException;

public class PlaceParserImpl implements PlaceParser {
  public static final String HASH = "#";
  public static final String TOP_LEVEL_SEPARATOR = ":";
  public static final String PLACE_SEPARATOR = ";";
  public static final String VALUE_SEPARATOR = "=";
  private static final String EMPTY_STRING = "";

  @Override
  public Map<AttributeKey, AttributeValue> parse(String token) throws InvalidUrlException {
    if (token == null) {
      throw new InvalidUrlException("Invalid path");
    }

    String[] tokens = token.split(PLACE_SEPARATOR);

    Map<AttributeKey, AttributeValue> result = new HashMap<AttributeKey, AttributeValue>();

    for (String t : tokens) {
      String[] pair = t.split(VALUE_SEPARATOR);

      AttributeKey key = AttributeKey.findByName(pair[0]);
      if (key == AttributeKey.NONE) {
        throw new InvalidUrlException("Invalid path " + token);
      }

      if (pair.length > 1) {
        AttributeValue value = new AttributeValue(pair[1]);
        result.put(key, value);
      } else {
        result.put(key, new AttributeValue(EMPTY_STRING));
      }
    }

    return result;
  }
}
