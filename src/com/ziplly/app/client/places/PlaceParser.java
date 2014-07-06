package com.ziplly.app.client.places;

import java.util.Map;

import com.ziplly.app.client.exceptions.InvalidUrlException;

public interface PlaceParser {
  public Map<AttributeKey, AttributeValue> parse(String token) throws InvalidUrlException;
}
