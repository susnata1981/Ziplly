package com.ziplly.app.client.widget.chart;

public class Value<T> {
	private String label;
	private T value;
	
	public Value() {
  }
	
	public Value(String label, T value) {
		this.label = label;
		this.value = value;
  }
	
	public void setValue(T value) {
		this.value = value;
	}
	
	T getValue() {
		return value;
	}

	public String getLabel() {
	  return label;
  }

	public void setLabel(String label) {
	  this.label = label;
  }
}
