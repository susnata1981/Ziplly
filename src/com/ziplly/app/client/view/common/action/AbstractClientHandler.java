package com.ziplly.app.client.view.common.action;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.widget.TweetWidget;

public abstract class AbstractClientHandler<T extends Result> extends DispatcherCallbackAsync<T> {
  protected TweetWidget widget;
  protected StringDefinitions stringDefinitions;
  
  public AbstractClientHandler(
      EventBus eventBus, 
      TweetWidget widget, 
      StringDefinitions stringDefinitions) {
    
    super(eventBus);
    this.widget = widget;
    this.stringDefinitions = stringDefinitions;
  }
}
