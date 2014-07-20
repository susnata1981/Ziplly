package com.ziplly.app.client.places;

import java.util.ArrayList;
import java.util.List;

public class AttributeValue {
  private static final String EMPTY_STRING = "";
  private List<String> values = new ArrayList<String>();

  public AttributeValue(String value) {
    addValue(value);
  }
  
  public List<String> getValues() {
    return values;
  }

  public void addValue(String value) {
    values.add(value);
  }
  
  public String value() {
    if (values.size() == 0) {
      return EMPTY_STRING;
    }
    
    assert(values.size() == 1);
    return values.get(0);
  }
  
  public int count() {
    return values.size();
  }
}
