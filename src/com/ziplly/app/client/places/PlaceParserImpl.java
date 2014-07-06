package com.ziplly.app.client.places;

import java.util.HashMap;
import java.util.Map;

import com.ziplly.app.client.exceptions.InvalidUrlException;

public class PlaceParserImpl implements PlaceParser {
  public static final String HASH = "#";
  public static final String TOP_LEVEL_SEPARATOR = ":";
  public static final String PLACE_SEPARATOR = ";";
  public static final String VALUE_SEPARATOR = "=";

  @Override
  public Map<AttributeKey, AttributeValue> parse(String token) throws InvalidUrlException {
    if (token == null) {
      throw new InvalidUrlException("Invalid path");
    }
    
    String [] tokens = token.split(PLACE_SEPARATOR);
    
    Map<AttributeKey, AttributeValue> result = new HashMap<AttributeKey, AttributeValue>();
    
    for(String t : tokens) {
      String [] pair = t.split(VALUE_SEPARATOR);

      if (pair.length != 2) {
        throw new InvalidUrlException("Invalid path "+token);
      }
      
      AttributeKey key = AttributeKey.findByName(pair[0]);
      if (key == AttributeKey.NONE) {
        throw new InvalidUrlException("Invalid path "+token);
      }
      
      AttributeValue value = new AttributeValue(pair[1]);
      result.put(key, value);
    }
    
    return result;
  }
}
