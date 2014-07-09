package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.github.gwtbootstrap.client.ui.constants.Trigger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class AbstractBaseAccountView extends AbstractView {

  public AbstractBaseAccountView(EventBus eventBus) {
    super(eventBus);
  }
  
  public void displayAccontUpdate(HTMLPanel accountUpdatePanel, List<PendingActionTypes> updates) {
    if (updates.size() == 0) {
      displayNoPendingActions(accountUpdatePanel);
      displayAccontUpdatePanel(true);
      return;
    }
    
    for(final PendingActionTypes update : updates) {
      Anchor anchor = createAnchorWithTooltip(update);
      FlowPanel panel = new FlowPanel();
      panel.addStyleName("padding-left");
      panel.add(anchor);
      accountUpdatePanel.add(panel);
    }

    displayAccontUpdatePanel(true);
  }

  private void displayNoPendingActions(HTMLPanel accountUpdatePanel) {
    FlowPanel panel = new FlowPanel();
    panel.addStyleName("padding-left");
    panel.add(new Label("No pending actions"));
    accountUpdatePanel.add(panel);
  }

  private Anchor createAnchorWithTooltip(final PendingActionTypes update) {
    final Tooltip tooltip = new Tooltip();
    tooltip.setText(update.learnMoreText());
    tooltip.setPlacement(Placement.TOP);
    tooltip.setText(update.learnMoreText());
    Anchor anchor = new Anchor(update.text());
    anchor.addStyleName("account-update-small-font");
    anchor.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        goTo(update.getPlace());
      }

    });

    tooltip.setWidget(anchor);
    tooltip.reconfigure();
    tooltip.setTrigger(Trigger.HOVER);
    return anchor;
  }
  
  public abstract void goTo(Place place);
  public abstract void displayAccontUpdatePanel(boolean b);
}
