package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class VirtuousCycleWidget extends Composite {

  private static VirtuousCycleWidgetUiBinder uiBinder = GWT
      .create(VirtuousCycleWidgetUiBinder.class);

  interface VirtuousCycleWidgetUiBinder extends UiBinder<Widget, VirtuousCycleWidget> {
  }

  public VirtuousCycleWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }
}
