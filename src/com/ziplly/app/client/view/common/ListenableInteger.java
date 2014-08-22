package com.ziplly.app.client.view.common;

import java.util.ArrayList;
import java.util.List;

public class ListenableInteger {
  private final List<PropertyChangeListener> listeners;
  private int oldValue;
  private int newValue;

  public ListenableInteger() {
    this.newValue = 0;
    this.oldValue = 0;
    this.listeners = new ArrayList<PropertyChangeListener>();
  }
  
  public void increment() {
    this.oldValue = newValue;
    this.newValue++;
    fireChangeEvent();
  }

  private void fireChangeEvent() {
    for(PropertyChangeListener listener : listeners) {
      listener.propertyChange(oldValue, newValue);
    }
  }
  
  public void addListener(PropertyChangeListener listener) {
    listeners.add(listener);
  }
  
  public static interface PropertyChangeListener {
    public void propertyChange(Object oldValue, Object newValue);
  }
}
