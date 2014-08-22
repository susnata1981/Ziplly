package com.ziplly.app.client.view.common;

public interface RenderingStatusCallback {
  /**
   * reports % rendering complete. 
   */
  void finished(double completedPercentage);
}
